package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.*;
import org.yearup.repository.OrderRepository;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.ShoppingCartRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutService {
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ProductService productService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProfileService profileService;

    public CheckoutService(OrderRepository orderRepository,
                           OrderLineItemRepository orderLineItemRepository,
                           ProductService productService,
                           ShoppingCartRepository shoppingCartRepository, ProfileService profileService) {
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.productService = productService;
        this.shoppingCartRepository = shoppingCartRepository;
        this.profileService = profileService;
    }

    public Order convertToOrder(int userId) {
        List<CartItem> existing = shoppingCartRepository.findByUserId(userId);
        if (existing.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        order.setShippingAmount(5.99);

        try {
            Profile userProfile = profileService.getProfile(userId);

            if (userProfile != null) {
                order.setAddress(userProfile.getAddress() != null && !userProfile.getAddress().isBlank() ? userProfile.getAddress() : "123 Main St");
                order.setCity(userProfile.getCity() != null && !userProfile.getCity().isBlank() ? userProfile.getCity() : "Default City");
                order.setState(userProfile.getState() != null && !userProfile.getState().isBlank() ? userProfile.getState() : "NY");

                try {
                    order.setZipCode(Integer.parseInt(userProfile.getZip()));
                } catch (Exception e) {
                    order.setZipCode(10001);
                }
            } else {
                order.setAddress("123 Main St");
                order.setCity("Default City");
                order.setState("NY");
                order.setZipCode(10001);
            }
        } catch (Exception e) {
            order.setAddress("123 Main St");
            order.setCity("Default City");
            order.setState("NY");
            order.setZipCode(10001);
        }

        order = orderRepository.save(order);

        for (CartItem item : existing) {
            Product product = productService.getById(item.getProductId());
            double itemPrice = product.getPrice();

            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrderId(order.getOrderId());
            lineItem.setProductId(item.getProductId());
            lineItem.setQuantity(item.getQuantity());
            lineItem.setSalesPrice(itemPrice);

            orderLineItemRepository.save(lineItem);
        }

        for (CartItem item : existing) {
            shoppingCartRepository.delete(item);
        }

        return order;
    }

}
