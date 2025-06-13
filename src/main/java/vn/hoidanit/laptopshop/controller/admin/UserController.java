package vn.hoidanit.laptopshop.controller.admin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.UserRepository;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;


@Controller
public class UserController {

    private final UserService userService;
    private final UploadService uploadService;

    
    public UserController(UserService userService, UploadService uploadService) {
    this.userService = userService;
    this.uploadService = uploadService;
    }
    
    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUser = this.userService.getAllUsersByEmail("1@gmail.com");
        System.out.println(arrUser);
        model.addAttribute("eric", "test");
        model.addAttribute("hoidanit", "from controller with model");
        return "hello";
    }

    @GetMapping("admin/user/create") //GET
        public String getCreateUserPage(Model model) {
            model.addAttribute("newUser", new User());
            return "/admin/user/create";
        }
    
    @RequestMapping("/admin/user")
        public String getUserPage(Model model) {
            List<User> users = this.userService.getAllUsers();
            model.addAttribute("users1", users);
            return "/admin/user/show";
        }

    

    @PostMapping(value = "/admin/user/create")
    public String createUserPage(Model model, @ModelAttribute("newUser") User hoidanit, @RequestParam("hoidanitFile") MultipartFile file) {


        //  private final ServletContext servletContext;
            
            String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");

            // this.userService.handleSaveUser(hoidanit);
            return "redirect:/admin/user";
        } 

    @RequestMapping("/admin/user/{id}")
        public String getUserDetailPage(Model model, @PathVariable long id) {
            User user = this.userService.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("id", id);
            return "/admin/user/detail";
        }


    @RequestMapping("/admin/user/update/{id}") 
        public String getUpdateUserPage (Model model, @PathVariable long id) {
            User currentUser = this.userService.getUserById(id);
            model.addAttribute("newUser", currentUser);
            return "/admin/user/update";
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
            return "/admin/user/delete";
        }

    @PostMapping("/admin/user/delete")
        public String postDeleteUser(Model model, @ModelAttribute("newUser") User hoidanit) {           
            this.userService.deleteUser(hoidanit.getId());
            return "redirect:/admin/user";
        }
    
}


