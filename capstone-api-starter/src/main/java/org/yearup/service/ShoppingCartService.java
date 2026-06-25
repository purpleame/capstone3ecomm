package org.yearup.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.util.List;

@Service
public class ShoppingCartService
{
    // a shopping cart is built from cart rows plus a product lookup for each row
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public List<CartItem> getByUserId(int userId)
    {
        return shoppingCartRepository.findByUserId(userId);
    }

    // add additional methods here

    public ShoppingCart getCart(int userId)
    {
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);
        ShoppingCart shoppingCart = new ShoppingCart();

        for (CartItem item : cartItems)
        {
            Product product = productService.getById(item.getProductId());

            ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
            shoppingCartItem.setProduct(product);
            shoppingCartItem.setQuantity(item.getQuantity());
            shoppingCartItem.setDiscountPercent(0.0);

            shoppingCart.add(shoppingCartItem);
        }
        return shoppingCart;
    }

    public ShoppingCart addProductToCart(int userId, int productId)
    {
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);
        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst()
                .orElse(null);

        if (existingItem != null)
        {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            shoppingCartRepository.save(existingItem);
        }
        else
        {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(1);

            shoppingCartRepository.save(newItem);
        }

        return getCart(userId);
    }


    public ShoppingCart updateCartItem(int userId, int productId, CartItem updatedItem)
    {
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);
        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst()
                .orElse(null);

        if (existingItem != null)
        {
            existingItem.setQuantity(updatedItem.getQuantity());
            shoppingCartRepository.save(existingItem);
        }

        return getCart(userId);
    }

    public ShoppingCart clearCart(int userId)
    {
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);
        for (CartItem item : cartItems)
        {
            shoppingCartRepository.delete(item);
        }

        return getCart(userId);
    }

    public ShoppingCart createCart(int userId, int productId, int initialQuantity)
    {
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);
        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst()
                .orElse(null);

        if (existingItem != null)
        {
            existingItem.setQuantity(existingItem.getQuantity() + initialQuantity);
            shoppingCartRepository.save(existingItem);
        }
        else
        {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(initialQuantity);

            shoppingCartRepository.save(newItem);
        }

        return getCart(userId);
    }
}
