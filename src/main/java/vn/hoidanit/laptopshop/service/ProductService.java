package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.Product_;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.ProductCriteriaDTO;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;
import vn.hoidanit.laptopshop.service.specification.ProductSpecs;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(ProductRepository productRepository, CartRepository cartRepository, CartDetailRepository cartDetailRepository , UserService userService, OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Product createProduct(Product product) {
        return this.productRepository.save(product);
    }

    

       
    


    public Page<Product> fetchProducts(Pageable pageable) {
        return this.productRepository.findAll( pageable);
    }


    public Page<Product> fetchProductsWithSpec(Pageable pageable, ProductCriteriaDTO productCriteriaDTO) {
        if (productCriteriaDTO.getTarget() == null 
        && productCriteriaDTO.getFactory() == null 
        && productCriteriaDTO.getPrice() == null) {
            return this.productRepository.findAll(pageable);
        }
        
        Specification<Product> combinedSpec = Specification.where(null);
        if (productCriteriaDTO.getTarget() != null &&  productCriteriaDTO.getTarget().isPresent()) {
            Specification<Product> currentSpecs = ProductSpecs.matchListTarget(productCriteriaDTO.getTarget().get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (productCriteriaDTO.getFactory() != null &&  productCriteriaDTO.getFactory().isPresent()) {
            Specification<Product> currentSpecs = ProductSpecs.matchListFactory(productCriteriaDTO.getFactory().get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (productCriteriaDTO.getPrice() != null &&  productCriteriaDTO.getPrice().isPresent()) {
            Specification<Product> currentSpecs = this.buildPriceSpecification(productCriteriaDTO.getPrice().get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }
        return this.productRepository.findAll(combinedSpec , pageable);
    }

    
    public Specification<Product> buildPriceSpecification(List<String> price) {
        Specification<Product> combinedSpec = Specification.where(null);
        
        for (String p : price) {
            double min = 0;
            double max = 0;
            
            switch (p) {
                case "duoi-10-trieu":
                    min = 0;
                    max = 10000000;
                    break;
                case "10-15-trieu":
                    min = 10000000;
                    max = 15000000;                   
                    break;
                case "15-20-trieu":
                    min = 15000000;
                    max = 20000000;                    
                    break;
                case "tren-20-trieu":
                    min = 20000000;
                    max = 200000000;                    
                    break;
            }
            if (min != 0 && max != 0) {
                Specification<Product> rangeSpec = ProductSpecs.matchMutiplePrice(min, max);
                combinedSpec = combinedSpec.or(rangeSpec);
            }

        }
        
        return combinedSpec;
    }
    //case 1
    // public Page<Product> fetchProductsWithSpec(Pageable pageable, double min) {
    //     return this.productRepository.findAll(ProductSpecs.minPrice(min), pageable);
    // }

    //case 2
    // public Page<Product> fetchProductsWithSpec(Pageable pageable, double max) {
    //     return this.productRepository.findAll(ProductSpecs.maxPrice(max), pageable);
    // }

    //case 3
    // public Page<Product> fetchProductsWithSpec(Pageable pageable, String factory) {
    //     return this.productRepository.findAll(ProductSpecs.matchFactory(factory), pageable);
    // }

    //case 4
    // public Page<Product> fetchProductsWithSpec(Pageable pageable, List<String> factory) {
    //     return this.productRepository.findAll(ProductSpecs.matchListFactory(factory), pageable);
    // }

    //case 5
    // public Page<Product> fetchProductsWithSpec(Pageable page, String price) {
    //     //eg: price 10toi-15-trieu
    //     if (price.equals("10-toi-15-trieu")) {
    //         double min = 10000000;
    //         double max = 15000000;
    //         return this.productRepository.findAll(ProductSpecs.matchPrice(min, max), page);
    //     }
    //     else if (price.equals("15-toi-30-trieu")) {
    //         double min = 15000000;
    //         double max = 30000000;
    //         return this.productRepository.findAll(ProductSpecs.matchPrice(min, max), page);
    //     }
    //     else {
    //         return this.productRepository.findAll(page);
    //     }
    // }

    // case 6
    // public Page<Product> fetchProductsWithSpec(Pageable page, List<String> price) {
    //     Specification<Product> combinedSpec = (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
    //     int count = 0;
    //     for (String p : price) {
    //         double min = 0;
    //         double max = 0;
            
    //         switch (p) {
    //             case "10-toi-15-trieu";
    //                 min = 10000000;
    //                 max = 15000000;
    //                 count++;
    //                 break;
    //             case "15-toi-20-trieu";
    //                 min = 15000000;
    //                 max = 20000000;
    //                 count++;
    //                 break;
    //             case "20-toi-30-trieu";
    //                 min = 20000000;
    //                 max = 30000000;
    //                 count++;
    //                 break;
    //         }
    //         if (min != 0 && max != 0) {
    //             Specification<Product> rangeSpec = ProductSpecs.matchMutiplePrice(min, max);
    //             combinedSpec = combinedSpec.or(rangeSpec);
    //         }

    //     }
    //     if (count == 0) {
    //         return this.productRepository.findAll(page);
    //     }
    //     return this.prioductRepository.findAll(combinedSpec, page);
    // }


    public void deleteProduct(long id) {
        this.productRepository.deleteById(id);
    }

    public Optional<Product> fetchProductById(long id) {
        return this.productRepository.findById(id);
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                Cart otherCart = new Cart();
                otherCart.setUser(user);
                otherCart.setSum(0);
                cart = this.cartRepository.save(otherCart);
            }
            Optional<Product> productOptional = this.productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product realProduct = productOptional.get();
                //Check xem san pham da tung duoc tem vao gio hang chua
                CartDetail oldDetail = this.cartDetailRepository.findByCartAndProduct(cart, realProduct);
                
                if(oldDetail == null) {
                    CartDetail cartDetail = new CartDetail();
                    cartDetail.setCart(cart);
                    cartDetail.setProduct(realProduct);
                    cartDetail.setPrice(realProduct.getPrice());
                    cartDetail.setQuantity(1);
                    this.cartDetailRepository.save(cartDetail);

                    //update sum

                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);
                }
                else {
                    oldDetail.setQuantity(oldDetail.getQuantity() + 1);
                    this.cartDetailRepository.save(oldDetail);
                }

                
            }
            
        }
    }

    public Cart fetchByUser(User user) {
        return this.cartRepository.findByUser(user);
    }


    public void handleRemoveCartDetail(long cartDetailId, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();

            Cart  currentCart = cartDetail.getCart();
            //delete cart-detail
            this.cartDetailRepository.deleteById(cartDetailId);
            //update sum
            if (currentCart.getSum() > 1) {
                //update current cart
                int s = currentCart.getSum() - 1;
                currentCart.setSum(s);
                session.setAttribute("sum", s);
                this.cartRepository.save(currentCart);
            }
            else {
                //delete cart :: sum == 1
                this.cartRepository.deleteById(currentCart.getId());
                session.setAttribute("sum", 0);

            }
        }
    }

    public void handleUpdateCartBeforeCheckOut(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cdOptional = this.cartDetailRepository.findById(cartDetail.getId());
            if (cdOptional.isPresent()) {
                CartDetail currentCartDetail = cdOptional.get();
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                this.cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    public void handlePlaceOrder(User user, HttpSession session, String receiverName, String receiverPhone, String receiverAddress) {
       

        

        //step 1 : get cart by user
        Cart cart = this.cartRepository.findByUser(user);
        if (cart != null) {
            List<CartDetail> cartDetails = cart.getCartDetails();

            if (cartDetails != null) {
                 //create order
                Order order = new Order();
                order.setUser(user);
                order.setReceiverName(receiverName);
                order.setReceiverAddress(receiverAddress);
                order.setReceiverPhone(receiverPhone);
                order.setStatus("PENDING");
                double sum = 0;

                for (CartDetail cd : cartDetails) {
                    sum += cd.getPrice();
                }
                order.setTotalPrice(sum);
                
                order = this.orderRepository.save(order);

                //create order detail
                for (CartDetail cd : cartDetails) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cd.getProduct());
                    orderDetail.setPrice(cd.getPrice());
                    orderDetail.setQuantity(cd.getQuantity());
                    this.orderDetailRepository.save(orderDetail);
                }

                //step 2: delete cart_detail and cart
                for (CartDetail cd : cartDetails) {
                    this.cartDetailRepository.deleteById(cd.getId());
                }
                this.cartRepository.deleteById(cart.getId());
                

                //step 3: update session
                session.setAttribute("sum", 0);
            }
        }




    }

}
