/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.ProductType;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author hcadavid
 */

 @RestController
 @RequestMapping("/restaurant")
public class OrdersAPIController {

    @Autowired
    private RestaurantOrderServicesStub restaurantOrderServicesStub;

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders() {
        Map<Integer, Order> tableOrders = restaurantOrderServicesStub.getTableOrders();
        Map<Integer, Map<String, Object>> ordersWithTotals = new HashMap<>();

        tableOrders.forEach((tableNumber, order) -> {
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("order", order);
            try {
                int total = restaurantOrderServicesStub.calculateTableBill(tableNumber);
                orderDetails.put("total", total);
            } catch (OrderServicesException e) {
                orderDetails.put("total", "Error calculating total: " + e.getMessage());
            }
            ordersWithTotals.put(tableNumber, orderDetails);
        });

        return new ResponseEntity<>(ordersWithTotals, HttpStatus.ACCEPTED);
    }

    @GetMapping("/orders/taxes")
    public ResponseEntity<?> getOrdersWithTaxes() {
        Map<Integer, Order> tableOrders = restaurantOrderServicesStub.getTableOrders();
        Map<Integer, Map<String, Object>> ordersWithTotals = new HashMap<>();

        tableOrders.forEach((tableNumber, order) -> {
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("order", order);
            try {
                int total = restaurantOrderServicesStub.calculateBillWithTaxes(tableNumber);
                orderDetails.put("total with taxes", total);
            } catch (OrderServicesException e) {
                orderDetails.put("total", "Error calculating total: " + e.getMessage());
            }
            ordersWithTotals.put(tableNumber, orderDetails);
        });

        return new ResponseEntity<>(ordersWithTotals, HttpStatus.ACCEPTED);
    }

    @GetMapping("/getBill/{number}")
    public ResponseEntity<?> getBill(@PathVariable("number") int number) {
        int bill = 0;
        try {
            bill = restaurantOrderServicesStub.calculateTableBill(number);
        } catch (OrderServicesException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error calculating bill: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Map<String, Object> billDetails = new HashMap<>();
        billDetails.put("bill", bill);
        return new ResponseEntity<>(billDetails, HttpStatus.ACCEPTED);
    }
    
    
    
}
