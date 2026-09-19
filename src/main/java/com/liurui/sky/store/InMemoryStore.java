package com.liurui.sky.store;

import com.liurui.sky.common.BusinessException;
import com.liurui.sky.model.AddressBook;
import com.liurui.sky.model.CartItem;
import com.liurui.sky.model.Category;
import com.liurui.sky.model.Dish;
import com.liurui.sky.model.LoginResponse;
import com.liurui.sky.model.Order;
import com.liurui.sky.model.OrderSubmitRequest;
import com.liurui.sky.model.Setmeal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Repository
public class InMemoryStore {

    private static final long CURRENT_CUSTOMER_ID = 1L;

    private final JdbcTemplate jdbcTemplate;

    public InMemoryStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> listCategories(Integer type) {
        if (type == null) {
            return jdbcTemplate.query("select * from category order by sort, id", categoryMapper());
        }
        return jdbcTemplate.query("select * from category where type = ? order by sort, id", categoryMapper(), type);
    }

    public Category saveCategory(Category category) {
        if (category.getSort() == null) {
            Integer nextSort = jdbcTemplate.queryForObject("select coalesce(max(sort), 0) + 1 from category", Integer.class);
            category.setSort(nextSort);
        }
        if (category.getEnabled() == null) {
            category.setEnabled(Boolean.TRUE);
        }
        if (category.getId() == null) {
            Long id = insert("""
                    insert into category (name, type, sort, enabled)
                    values (?, ?, ?, ?)
                    """, category.getName(), category.getType(), category.getSort(), category.getEnabled());
            category.setId(id);
        } else {
            int rows = jdbcTemplate.update("""
                    update category set name = ?, type = ?, sort = ?, enabled = ?
                    where id = ?
                    """, category.getName(), category.getType(), category.getSort(), category.getEnabled(), category.getId());
            ensureUpdated(rows, "分类不存在");
        }
        return category;
    }

    public void enableCategory(Long id, Boolean enabled) {
        ensureUpdated(jdbcTemplate.update("update category set enabled = ? where id = ?", enabled, id), "分类不存在");
    }

    public List<Dish> listDishes(Long categoryId, Boolean onlyEnabled) {
        if (categoryId != null && Boolean.TRUE.equals(onlyEnabled)) {
            return jdbcTemplate.query("select * from dish where category_id = ? and enabled = true order by id", dishMapper(), categoryId);
        }
        if (categoryId != null) {
            return jdbcTemplate.query("select * from dish where category_id = ? order by id", dishMapper(), categoryId);
        }
        if (Boolean.TRUE.equals(onlyEnabled)) {
            return jdbcTemplate.query("select * from dish where enabled = true order by id", dishMapper());
        }
        return jdbcTemplate.query("select * from dish order by id", dishMapper());
    }

    public Dish saveDish(Dish dish) {
        requireExists("select count(*) from category where id = ?", dish.getCategoryId(), "菜品分类不存在");
        if (dish.getEnabled() == null) {
            dish.setEnabled(Boolean.TRUE);
        }
        if (dish.getId() == null) {
            Long id = insert("""
                    insert into dish (name, category_id, price, description, image, enabled)
                    values (?, ?, ?, ?, ?, ?)
                    """, dish.getName(), dish.getCategoryId(), dish.getPrice(), dish.getDescription(), dish.getImage(), dish.getEnabled());
            dish.setId(id);
        } else {
            int rows = jdbcTemplate.update("""
                    update dish set name = ?, category_id = ?, price = ?, description = ?, image = ?, enabled = ?
                    where id = ?
                    """, dish.getName(), dish.getCategoryId(), dish.getPrice(), dish.getDescription(), dish.getImage(), dish.getEnabled(), dish.getId());
            ensureUpdated(rows, "菜品不存在");
        }
        return dish;
    }

    public void enableDish(Long id, Boolean enabled) {
        ensureUpdated(jdbcTemplate.update("update dish set enabled = ? where id = ?", enabled, id), "菜品不存在");
    }

    public List<Setmeal> listSetmeals(Long categoryId, Boolean onlyEnabled) {
        String sql;
        Object[] args = new Object[0];
        if (categoryId != null && Boolean.TRUE.equals(onlyEnabled)) {
            sql = "select * from setmeal where category_id = ? and enabled = true order by id";
            args = new Object[]{categoryId};
        } else if (categoryId != null) {
            sql = "select * from setmeal where category_id = ? order by id";
            args = new Object[]{categoryId};
        } else if (Boolean.TRUE.equals(onlyEnabled)) {
            sql = "select * from setmeal where enabled = true order by id";
        } else {
            sql = "select * from setmeal order by id";
        }
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Setmeal setmeal = setmealMapper().mapRow(rs, rowNum);
            setmeal.setDishIds(listSetmealDishIds(setmeal.getId()));
            return setmeal;
        }, args);
    }

    @Transactional
    public Setmeal saveSetmeal(Setmeal setmeal) {
        requireExists("select count(*) from category where id = ?", setmeal.getCategoryId(), "套餐分类不存在");
        for (Long dishId : setmeal.getDishIds()) {
            requireExists("select count(*) from dish where id = ?", dishId, "套餐中的菜品不存在");
        }
        if (setmeal.getEnabled() == null) {
            setmeal.setEnabled(Boolean.TRUE);
        }
        if (setmeal.getId() == null) {
            Long id = insert("""
                    insert into setmeal (name, category_id, price, description, enabled)
                    values (?, ?, ?, ?, ?)
                    """, setmeal.getName(), setmeal.getCategoryId(), setmeal.getPrice(), setmeal.getDescription(), setmeal.getEnabled());
            setmeal.setId(id);
        } else {
            int rows = jdbcTemplate.update("""
                    update setmeal set name = ?, category_id = ?, price = ?, description = ?, enabled = ?
                    where id = ?
                    """, setmeal.getName(), setmeal.getCategoryId(), setmeal.getPrice(), setmeal.getDescription(), setmeal.getEnabled(), setmeal.getId());
            ensureUpdated(rows, "套餐不存在");
            jdbcTemplate.update("delete from setmeal_dish where setmeal_id = ?", setmeal.getId());
        }
        for (Long dishId : setmeal.getDishIds()) {
            jdbcTemplate.update("insert into setmeal_dish (setmeal_id, dish_id) values (?, ?)", setmeal.getId(), dishId);
        }
        return setmeal;
    }

    public void enableSetmeal(Long id, Boolean enabled) {
        ensureUpdated(jdbcTemplate.update("update setmeal set enabled = ? where id = ?", enabled, id), "套餐不存在");
    }

    public List<AddressBook> listAddresses() {
        return jdbcTemplate.query("select * from address_book where customer_id = ? order by default_address desc, id desc",
                addressMapper(), CURRENT_CUSTOMER_ID);
    }

    @Transactional
    public AddressBook saveAddress(AddressBook address) {
        if (Boolean.TRUE.equals(address.getDefaultAddress())) {
            clearDefaultAddress();
        } else if (address.getDefaultAddress() == null) {
            Integer count = jdbcTemplate.queryForObject("select count(*) from address_book where customer_id = ?", Integer.class, CURRENT_CUSTOMER_ID);
            address.setDefaultAddress(count == 0);
        }
        if (address.getId() == null) {
            Long id = insert("""
                    insert into address_book (customer_id, consignee, phone, detail, default_address)
                    values (?, ?, ?, ?, ?)
                    """, CURRENT_CUSTOMER_ID, address.getConsignee(), address.getPhone(), address.getDetail(), address.getDefaultAddress());
            address.setId(id);
        } else {
            int rows = jdbcTemplate.update("""
                    update address_book set consignee = ?, phone = ?, detail = ?, default_address = ?
                    where id = ? and customer_id = ?
                    """, address.getConsignee(), address.getPhone(), address.getDetail(), address.getDefaultAddress(), address.getId(), CURRENT_CUSTOMER_ID);
            ensureUpdated(rows, "地址不存在");
        }
        return address;
    }

    @Transactional
    public AddressBook defaultAddress(Long id) {
        requireExists("select count(*) from address_book where id = ? and customer_id = ?", id, CURRENT_CUSTOMER_ID, "地址不存在");
        clearDefaultAddress();
        jdbcTemplate.update("update address_book set default_address = true where id = ? and customer_id = ?", id, CURRENT_CUSTOMER_ID);
        return findAddress(id);
    }

    public List<CartItem> listCart() {
        return jdbcTemplate.query("select * from cart_item where customer_id = ? order by id", cartMapper(), CURRENT_CUSTOMER_ID);
    }

    public CartItem addCartItem(CartItem item) {
        if (item.getDishId() == null && item.getSetmealId() == null) {
            throw new BusinessException("购物车必须选择菜品或套餐");
        }
        if (item.getNumber() == null) {
            item.setNumber(1);
        }
        if (item.getDishId() != null) {
            Dish dish = queryOne("select * from dish where id = ? and enabled = true", dishMapper(), "菜品不存在或已停售", item.getDishId());
            item.setName(dish.getName());
            item.setAmount(dish.getPrice());
        }
        if (item.getSetmealId() != null) {
            Setmeal setmeal = queryOne("select * from setmeal where id = ? and enabled = true", setmealMapper(), "套餐不存在或已停售", item.getSetmealId());
            item.setName(setmeal.getName());
            item.setAmount(setmeal.getPrice());
        }
        Long id = insert("""
                insert into cart_item (customer_id, name, amount, number, dish_id, setmeal_id)
                values (?, ?, ?, ?, ?, ?)
                """, CURRENT_CUSTOMER_ID, item.getName(), item.getAmount(), item.getNumber(), item.getDishId(), item.getSetmealId());
        item.setId(id);
        return item;
    }

    public void clearCart() {
        jdbcTemplate.update("delete from cart_item where customer_id = ?", CURRENT_CUSTOMER_ID);
    }

    @Transactional
    public Order submitOrder(OrderSubmitRequest request) {
        requireExists("select count(*) from address_book where id = ? and customer_id = ?", request.getAddressId(), CURRENT_CUSTOMER_ID, "收货地址不存在");
        List<CartItem> cart = listCart();
        if (cart.isEmpty()) {
            throw new BusinessException("购物车为空");
        }
        BigDecimal amount = cart.stream()
                .map(item -> item.getAmount().multiply(BigDecimal.valueOf(item.getNumber())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDateTime now = LocalDateTime.now();
        String number = "SKY" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Long orderId = insert("""
                insert into orders (number, status, amount, address_id, order_time, remark)
                values (?, ?, ?, ?, ?, ?)
                """, number, 1, amount, request.getAddressId(), Timestamp.valueOf(now), request.getRemark());
        for (CartItem item : cart) {
            jdbcTemplate.update("""
                    insert into order_item (order_id, name, amount, number, dish_id, setmeal_id)
                    values (?, ?, ?, ?, ?, ?)
                    """, orderId, item.getName(), item.getAmount(), item.getNumber(), item.getDishId(), item.getSetmealId());
        }
        clearCart();
        return findOrder(orderId);
    }

    public List<Order> listOrders() {
        return jdbcTemplate.query("select * from orders order by order_time desc", orderMapper());
    }

    public Order updateOrderStatus(Long id, Integer status) {
        ensureUpdated(jdbcTemplate.update("update orders set status = ? where id = ?", status, id), "订单不存在");
        return findOrder(id);
    }

    public boolean shopOpen() {
        Boolean open = jdbcTemplate.queryForObject("select open_flag from shop_status where id = 1", Boolean.class);
        return Boolean.TRUE.equals(open);
    }

    public LoginResponse loginEmployee(String username, String password) {
        return queryOne("""
                select id, name from employee
                where username = ? and password = ? and enabled = true
                """, (rs, rowNum) -> new LoginResponse(rs.getLong("id"), rs.getString("name"), "dev-admin-token-" + rs.getLong("id")),
                "账号或密码错误", username, password);
    }

    public LoginResponse loginCustomer(String username, String password) {
        return queryOne("""
                select id, name from customer
                where username = ? and password = ? and enabled = true
                """, (rs, rowNum) -> new LoginResponse(rs.getLong("id"), rs.getString("name"), "dev-user-token-" + rs.getLong("id")),
                "账号或密码错误", username, password);
    }

    public void updateShopOpen(boolean open) {
        jdbcTemplate.update("merge into shop_status (id, open_flag) key(id) values (1, ?)", open);
    }

    public DashboardStats dashboardStats() {
        Integer categoryCount = jdbcTemplate.queryForObject("select count(*) from category", Integer.class);
        Integer dishCount = jdbcTemplate.queryForObject("select count(*) from dish", Integer.class);
        Integer setmealCount = jdbcTemplate.queryForObject("select count(*) from setmeal", Integer.class);
        Integer orderCount = jdbcTemplate.queryForObject("select count(*) from orders", Integer.class);
        BigDecimal turnover = jdbcTemplate.queryForObject("select coalesce(sum(amount), 0) from orders", BigDecimal.class);
        return new DashboardStats(categoryCount, dishCount, setmealCount, orderCount, turnover, shopOpen());
    }

    private AddressBook findAddress(Long id) {
        return queryOne("select * from address_book where id = ?", addressMapper(), "地址不存在", id);
    }

    private Order findOrder(Long id) {
        Order order = queryOne("select * from orders where id = ?", orderMapper(), "订单不存在", id);
        order.setItems(jdbcTemplate.query("select * from order_item where order_id = ? order by id", orderItemMapper(), id));
        return order;
    }

    private List<Long> listSetmealDishIds(Long setmealId) {
        return jdbcTemplate.queryForList("select dish_id from setmeal_dish where setmeal_id = ? order by dish_id", Long.class, setmealId);
    }

    private void clearDefaultAddress() {
        jdbcTemplate.update("update address_book set default_address = false where customer_id = ?", CURRENT_CUSTOMER_ID);
    }

    private Long insert(String sql, Object... args) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int index = 0; index < args.length; index++) {
                statement.setObject(index + 1, args[index]);
            }
            return statement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void requireExists(String sql, Object id, String message) {
        requireExists(sql, new Object[]{id}, message);
    }

    private void requireExists(String sql, Object id, Object secondId, String message) {
        requireExists(sql, new Object[]{id, secondId}, message);
    }

    private void requireExists(String sql, Object[] args, String message) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
        if (count == null || count == 0) {
            throw new BusinessException(message);
        }
    }

    private void ensureUpdated(int rows, String message) {
        if (rows == 0) {
            throw new BusinessException(message);
        }
    }

    private <T> T queryOne(String sql, RowMapper<T> mapper, String message, Object... args) {
        List<T> values = jdbcTemplate.query(sql, mapper, args);
        if (values.isEmpty()) {
            throw new BusinessException(message);
        }
        return values.get(0);
    }

    private RowMapper<Category> categoryMapper() {
        return (rs, rowNum) -> {
            Category category = new Category();
            category.setId(rs.getLong("id"));
            category.setName(rs.getString("name"));
            category.setType(rs.getInt("type"));
            category.setSort(rs.getInt("sort"));
            category.setEnabled(rs.getBoolean("enabled"));
            return category;
        };
    }

    private RowMapper<Dish> dishMapper() {
        return (rs, rowNum) -> {
            Dish dish = new Dish();
            dish.setId(rs.getLong("id"));
            dish.setName(rs.getString("name"));
            dish.setCategoryId(rs.getLong("category_id"));
            dish.setPrice(rs.getBigDecimal("price"));
            dish.setDescription(rs.getString("description"));
            dish.setImage(rs.getString("image"));
            dish.setEnabled(rs.getBoolean("enabled"));
            return dish;
        };
    }

    private RowMapper<Setmeal> setmealMapper() {
        return (rs, rowNum) -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(rs.getLong("id"));
            setmeal.setName(rs.getString("name"));
            setmeal.setCategoryId(rs.getLong("category_id"));
            setmeal.setPrice(rs.getBigDecimal("price"));
            setmeal.setDescription(rs.getString("description"));
            setmeal.setEnabled(rs.getBoolean("enabled"));
            return setmeal;
        };
    }

    private RowMapper<AddressBook> addressMapper() {
        return (rs, rowNum) -> {
            AddressBook address = new AddressBook();
            address.setId(rs.getLong("id"));
            address.setConsignee(rs.getString("consignee"));
            address.setPhone(rs.getString("phone"));
            address.setDetail(rs.getString("detail"));
            address.setDefaultAddress(rs.getBoolean("default_address"));
            return address;
        };
    }

    private RowMapper<CartItem> cartMapper() {
        return (rs, rowNum) -> {
            CartItem item = new CartItem();
            item.setId(rs.getLong("id"));
            item.setName(rs.getString("name"));
            item.setAmount(rs.getBigDecimal("amount"));
            item.setNumber(rs.getInt("number"));
            item.setDishId(nullableLong(rs.getObject("dish_id")));
            item.setSetmealId(nullableLong(rs.getObject("setmeal_id")));
            return item;
        };
    }

    private RowMapper<CartItem> orderItemMapper() {
        return (rs, rowNum) -> {
            CartItem item = new CartItem();
            item.setId(rs.getLong("id"));
            item.setName(rs.getString("name"));
            item.setAmount(rs.getBigDecimal("amount"));
            item.setNumber(rs.getInt("number"));
            item.setDishId(nullableLong(rs.getObject("dish_id")));
            item.setSetmealId(nullableLong(rs.getObject("setmeal_id")));
            return item;
        };
    }

    private RowMapper<Order> orderMapper() {
        return (rs, rowNum) -> {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setNumber(rs.getString("number"));
            order.setStatus(rs.getInt("status"));
            order.setAmount(rs.getBigDecimal("amount"));
            order.setAddressId(rs.getLong("address_id"));
            order.setOrderTime(rs.getTimestamp("order_time").toLocalDateTime());
            order.setRemark(rs.getString("remark"));
            return order;
        };
    }

    private Long nullableLong(Object value) {
        if (value == null) {
            return null;
        }
        return ((Number) value).longValue();
    }

    public record DashboardStats(Integer categoryCount, Integer dishCount, Integer setmealCount,
                                 Integer orderCount, BigDecimal turnover, Boolean shopOpen) {
    }
}
