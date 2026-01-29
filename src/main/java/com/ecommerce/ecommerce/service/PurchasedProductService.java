package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.models.BulkPurchaseRequest;
import com.ecommerce.ecommerce.models.ProductResponse;
import com.ecommerce.ecommerce.models.PurchaseProductRequest;
import com.ecommerce.ecommerce.models.PurchasedProductResponse;
import com.ecommerce.ecommerce.repositories.OrderRepository;
import com.ecommerce.ecommerce.repositories.ProductRepository;
import com.ecommerce.ecommerce.repositories.PurchasedProductRepository;
import com.ecommerce.ecommerce.repositories.UserRepository;
import com.ecommerce.ecommerce.schemas.EcommerceUsers;
import com.ecommerce.ecommerce.schemas.Orders;
import com.ecommerce.ecommerce.schemas.Products;
import com.ecommerce.ecommerce.schemas.PurchasedProducts;
import com.ecommerce.ecommerce.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchasedProductService {

    private final PurchasedProductRepository purchasedProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public ProductResponse<PurchasedProductResponse> purchaseProduct(String token, PurchaseProductRequest request) {
        try {
            String email = jwtUtil.extractSubject(token);
            Optional<EcommerceUsers> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ProductResponse.<PurchasedProductResponse>builder()
                        .status("error")
                        .message("User not found")
                        .build();
            }

            Optional<Products> productOpt = productRepository.findById(request.getProductId());
            if (productOpt.isEmpty()) {
                return ProductResponse.<PurchasedProductResponse>builder()
                        .status("error")
                        .message("Product not found")
                        .build();
            }

            Products product = productOpt.get();
            int quantity = request.getQuantity() > 0 ? request.getQuantity() : 1;

            if (product.getQuantity() < quantity) {
                return ProductResponse.<PurchasedProductResponse>builder()
                        .status("error")
                        .message("Insufficient stock available")
                        .build();
            }

            BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));

            Orders order = new Orders();
            order.setUserId(userOpt.get().getId());
            order.setTotalAmount(totalAmount);
            order.setStatus("COMPLETED");
            Orders savedOrder = orderRepository.save(order);

            PurchasedProducts purchasedProduct = new PurchasedProducts();
            purchasedProduct.setProductId(product.getId());
            purchasedProduct.setUserId(userOpt.get().getId());
            purchasedProduct.setQuantity(quantity);
            purchasedProduct.setOrderId(savedOrder.getId());
            purchasedProduct.setPriceAtPurchase(product.getPrice());

            PurchasedProducts saved = purchasedProductRepository.save(purchasedProduct);

            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            log.info("Product purchased successfully. OrderId: {}, PurchaseId: {}, ProductId: {}, UserId: {}",
                    savedOrder.getId(), saved.getId(), product.getId(), userOpt.get().getId());

            PurchasedProductResponse response = PurchasedProductResponse.builder()
                    .id(saved.getId())
                    .productId(product.getId())
                    .productTitle(product.getTitle())
                    .productDescription(product.getDescription())
                    .productPrice(product.getPrice())
                    .productUrl(product.getProductUrl())
                    .quantity(quantity)
                    .purchasedAt(saved.getCreatedAt())
                    .build();

            return ProductResponse.<PurchasedProductResponse>builder()
                    .status("success")
                    .message("Product purchased successfully")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("Error purchasing product", e);
            return ProductResponse.<PurchasedProductResponse>builder()
                    .status("error")
                    .message("Failed to purchase product: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public ProductResponse<List<PurchasedProductResponse>> purchaseProducts(String token, BulkPurchaseRequest request) {
        try {
            String email = jwtUtil.extractSubject(token);
            Optional<EcommerceUsers> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ProductResponse.<List<PurchasedProductResponse>>builder()
                        .status("error")
                        .message("User not found")
                        .build();
            }

            if (request.getProducts() == null || request.getProducts().isEmpty()) {
                return ProductResponse.<List<PurchasedProductResponse>>builder()
                        .status("error")
                        .message("No products provided")
                        .build();
            }

            Long userId = userOpt.get().getId();
            List<PurchasedProductResponse> purchasedList = new ArrayList<>();

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<Products> productsToUpdate = new ArrayList<>();
            List<PurchaseProductRequest> validatedItems = new ArrayList<>();

            for (PurchaseProductRequest item : request.getProducts()) {
                Optional<Products> productOpt = productRepository.findById(item.getProductId());
                if (productOpt.isEmpty()) {
                    return ProductResponse.<List<PurchasedProductResponse>>builder()
                            .status("error")
                            .message("Product not found: ID " + item.getProductId())
                            .build();
                }

                Products product = productOpt.get();
                int quantity = item.getQuantity() > 0 ? item.getQuantity() : 1;

                if (product.getQuantity() < quantity) {
                    return ProductResponse.<List<PurchasedProductResponse>>builder()
                            .status("error")
                            .message("Insufficient stock for product: " + product.getTitle())
                            .build();
                }

                totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                productsToUpdate.add(product);
                validatedItems.add(item);
            }

            Orders order = new Orders();
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setStatus("COMPLETED");
            Orders savedOrder = orderRepository.save(order);

            for (int i = 0; i < validatedItems.size(); i++) {
                PurchaseProductRequest item = validatedItems.get(i);
                Products product = productsToUpdate.get(i);
                int quantity = item.getQuantity() > 0 ? item.getQuantity() : 1;

                PurchasedProducts purchasedProduct = new PurchasedProducts();
                purchasedProduct.setProductId(product.getId());
                purchasedProduct.setUserId(userId);
                purchasedProduct.setQuantity(quantity);
                purchasedProduct.setOrderId(savedOrder.getId());
                purchasedProduct.setPriceAtPurchase(product.getPrice());

                PurchasedProducts saved = purchasedProductRepository.save(purchasedProduct);

                product.setQuantity(product.getQuantity() - quantity);
                productRepository.save(product);

                purchasedList.add(PurchasedProductResponse.builder()
                        .id(saved.getId())
                        .productId(product.getId())
                        .productTitle(product.getTitle())
                        .productDescription(product.getDescription())
                        .productPrice(product.getPrice())
                        .productUrl(product.getProductUrl())
                        .quantity(quantity)
                        .purchasedAt(saved.getCreatedAt())
                        .build());
            }

            log.info("Bulk purchase completed. OrderId: {}, UserId: {}, Products: {}",
                    savedOrder.getId(), userId, purchasedList.size());

            return ProductResponse.<List<PurchasedProductResponse>>builder()
                    .status("success")
                    .message("Products purchased successfully")
                    .data(purchasedList)
                    .build();
        } catch (Exception e) {
            log.error("Error during bulk purchase", e);
            return ProductResponse.<List<PurchasedProductResponse>>builder()
                    .status("error")
                    .message("Failed to purchase products: " + e.getMessage())
                    .build();
        }
    }

    public ProductResponse<List<PurchasedProductResponse>> getPurchasedProducts(String token) {
        try {
            String email = jwtUtil.extractSubject(token);
            Optional<EcommerceUsers> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ProductResponse.<List<PurchasedProductResponse>>builder()
                        .status("error")
                        .message("User not found")
                        .build();
            }

            List<PurchasedProducts> purchases = purchasedProductRepository
                    .findByUserIdOrderByCreatedAtDesc(userOpt.get().getId());

            List<PurchasedProductResponse> responseList = purchases.stream()
                    .map(purchase -> {
                        Optional<Products> productOpt = productRepository.findById(purchase.getProductId());
                        Products product = productOpt.orElse(null);

                        return PurchasedProductResponse.builder()
                                .id(purchase.getId())
                                .productId(purchase.getProductId())
                                .productTitle(product != null ? product.getTitle() : "Product not available")
                                .productDescription(product != null ? product.getDescription() : null)
                                .productPrice(product != null ? product.getPrice() : null)
                                .productUrl(product != null ? product.getProductUrl() : null)
                                .quantity(purchase.getQuantity())
                                .purchasedAt(purchase.getCreatedAt())
                                .build();
                    })
                    .toList();

            return ProductResponse.<List<PurchasedProductResponse>>builder()
                    .status("success")
                    .message("Purchased products fetched successfully")
                    .data(responseList)
                    .build();
        } catch (Exception e) {
            log.error("Error fetching purchased products", e);
            return ProductResponse.<List<PurchasedProductResponse>>builder()
                    .status("error")
                    .message("Failed to fetch purchased products: " + e.getMessage())
                    .build();
        }
    }
}
