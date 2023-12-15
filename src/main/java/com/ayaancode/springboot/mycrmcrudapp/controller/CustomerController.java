package com.ayaancode.springboot.mycrmcrudapp.controller;

import com.ayaancode.springboot.mycrmcrudapp.dto.CustomerDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Value("${api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public CustomerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Authentication endpoint
    @PostMapping("/authenticate")
    public String authenticateUser(@RequestParam("loginId") String loginId,
                                   @RequestParam("password") String password,
                                   HttpSession session) {
        String authenticationUrl = apiUrl + "/assignment_auth.jsp";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(loginId, password);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(authenticationUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String accessToken = response.getBody();
            session.setAttribute("access_token", accessToken);
            return "redirect:/customers/list";
        } else {
            // Handle authentication failure
            return "login";
        }
    }

    // Get customer list endpoint
    @GetMapping("/list")
    public String getCustomerList(Model model, HttpSession session) {
        String customerListUrl = apiUrl + "/assignment.jsp?cmd=get_customer_list";

        HttpHeaders headers = new HttpHeaders();
        String authorization = "Bearer " + session.getAttribute("access_token");
        headers.set("Authorization", authorization);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<CustomerDTO[]> response = restTemplate.exchange(customerListUrl, HttpMethod.GET, requestEntity, CustomerDTO[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            CustomerDTO[] customers = response.getBody();
            model.addAttribute("customers", customers);
            return "customerList";
        } else {
            // Handle API request failure
            return "error";
        }
    }

    // Show add customer form
    @GetMapping("/add")
    public String showAddCustomerForm() {
        return "addCustomer";
    }

    // Create customer endpoint
    @PostMapping("/create")
    public String createCustomer(@ModelAttribute CustomerDTO customerDTO, HttpSession session) {
        String createCustomerUrl = apiUrl + "/assignment.jsp?cmd=create";

        HttpHeaders headers = new HttpHeaders();
        String authorization = "Bearer " + session.getAttribute("access_token");
        headers.set("Authorization", authorization);

        HttpEntity<CustomerDTO> requestEntity = new HttpEntity<>(customerDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(createCustomerUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/customers/list";
        } else {
            // Handle API request failure
            return "error";
        }
    }

    // Delete customer endpoint
    @PostMapping("/delete/{uuid}")
    public String deleteCustomer(@PathVariable String uuid, HttpSession session) {
        String deleteCustomerUrl = apiUrl + "/assignment.jsp?cmd=delete&uuid=" + uuid;

        HttpHeaders headers = new HttpHeaders();
        String authorization = "Bearer " + session.getAttribute("access_token");
        headers.set("Authorization", authorization);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(deleteCustomerUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/customers/list";
        } else {
            // Handle API request failure
            return "error";
        }
    }

    // Update customer endpoint
    @PostMapping("/update/{uuid}")
    public String updateCustomer(@PathVariable String uuid, @ModelAttribute CustomerDTO customerDTO, HttpSession session) {
        String updateCustomerUrl = apiUrl + "/assignment.jsp?cmd=update&uuid=" + uuid;

        HttpHeaders headers = new HttpHeaders();
        String authorization = "Bearer " + session.getAttribute("access_token");
        headers.set("Authorization", authorization);

        HttpEntity<CustomerDTO> requestEntity = new HttpEntity<>(customerDTO, headers);
        ResponseEntity<String> response = restTemplate.exchange(updateCustomerUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/customers/list";
        } else {
            // Handle API request failure
            return "error";
        }
    }
}
