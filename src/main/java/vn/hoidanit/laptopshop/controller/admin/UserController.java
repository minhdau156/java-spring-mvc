package vn.hoidanit.laptopshop.controller.admin;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;

import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;


@Controller
public class UserController {

    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;
    
    public UserController(UserService userService, UploadService uploadService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.uploadService = uploadService;
    this.passwordEncoder = passwordEncoder;
    }
    
    

    @GetMapping("admin/user/create") //GET
        public String getCreateUserPage(Model model) {
            model.addAttribute("newUser", new User());
            return "admin/user/create";
        }
    
    @RequestMapping("/admin/user")
        public String getUserPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
            int page = 1;
        try {
            if (pageOptional.isPresent()) {
                //convert from string to int
                page = Integer.parseInt(pageOptional.get());
            }
            else {

            }
        }
        catch (Exception e) {

        }
            
            
            Pageable pageable = PageRequest.of(page - 1, 1);
            Page<User> users = this.userService.getAllUsers(pageable);
            List<User> users1 = users.getContent();
            
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages2", users.getTotalPages());
            
            model.addAttribute("users1", users1);
            return "admin/user/show";
        }

    

    @PostMapping(value = "/admin/user/create")
    public String createUserPage(Model model, @ModelAttribute("newUser") @Valid User hoidanit,  BindingResult newUserBindingResult, @RequestParam("hoidanitFile") MultipartFile file) {

            List<FieldError> errors = newUserBindingResult.getFieldErrors();

            for (FieldError error : errors) {
                System.out.println(">>>>" + error.getField() + " - " + error.getDefaultMessage());
            }

            if (newUserBindingResult.hasErrors()){
                return "admin/user/create";
            }
            
            String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
            String hashPassword = this.passwordEncoder.encode(hoidanit.getPassword());
            
            hoidanit.setAvatar(avatar);
            hoidanit.setPassword(hashPassword);
            hoidanit.setRole(this.userService.getRoleByName(hoidanit.getRole().getName()));
            
            this.userService.handleSaveUser(hoidanit);

            return "redirect:/admin/user";
        } 

    @RequestMapping("/admin/user/{id}")
        public String getUserDetailPage(Model model, @PathVariable long id) {
            User user = this.userService.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("id", id);
            return "admin/user/detail";
        }


    @RequestMapping("/admin/user/update/{id}") 
        public String getUpdateUserPage (Model model, @PathVariable long id) {
            User currentUser = this.userService.getUserById(id);
            model.addAttribute("newUser", currentUser);
            return "admin/user/update";
        }
    
    @PostMapping("/admin/user/update") 
        public String postUpdateUser (Model model, @ModelAttribute("newUser") User hoidanit) {
            User currentUser = this.userService.getUserById(hoidanit.getId());
            if (currentUser != null) {
                currentUser.setAddress(hoidanit.getAddress());
                currentUser.setFullName(hoidanit.getFullName());
                currentUser.setPhone(hoidanit.getPhone());

                this.userService.handleSaveUser(currentUser);
            }
            return "redirect:/admin/user";
        }
    

    @GetMapping("/admin/user/delete/{id}")
        public String getDeleteUserPage(Model model, @PathVariable long id) {           
            model.addAttribute("id", id);
            // User user = new User();
            // user.setId(id);
            model.addAttribute("newUser", new User());
            return "admin/user/delete";
        }

    @PostMapping("/admin/user/delete")
        public String postDeleteUser(Model model, @ModelAttribute("newUser") User hoidanit) {           
            this.userService.deleteUser(hoidanit.getId());
            return "redirect:/admin/user";
        }
    
}


