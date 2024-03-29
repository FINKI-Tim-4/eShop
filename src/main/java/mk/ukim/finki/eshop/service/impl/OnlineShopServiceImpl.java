package mk.ukim.finki.eshop.service.impl;

import mk.ukim.finki.eshop.model.Order;
import mk.ukim.finki.eshop.model.enumeration.OrderStatus;
import mk.ukim.finki.eshop.repository.OrderRepository;
import mk.ukim.finki.eshop.service.OnlineShopService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OnlineShopServiceImpl implements OnlineShopService {

    private final OrderRepository repository;

    public OnlineShopServiceImpl(OrderRepository repository) {
        this.repository = repository;
    }

    private String[] getMonths(){
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths();
    }

    @Override
    @Transactional
    public List<Double> profitForYearPerMonths(int year) {
        List<Double> perMonth = new ArrayList<>(12);
        List<Order> orders = this.repository.findAllByOrderStatus(OrderStatus.COMPLETED)
                .stream()
                .filter(order -> order.getCreateTime().getYear()==year )
                .collect(Collectors.toList());
        IntStream.range(0,12).forEach(i->perMonth.add(0.0));
        if(orders.size()>0) {
            IntStream.range(0, 12)
                    .forEach(i ->
                            perMonth.set(i,
                                    (orders.stream()
                                            .filter(order -> order.getCreateTime().getMonthValue() == i + 1)
                                            .count() <= 0) ? 0.0 :
                                    orders.stream()
                                            .filter(order ->  order.getCreateTime().getMonthValue() == i + 1)
                                            .mapToDouble(Order::getTotal)
                                            .sum()
                    )
            );
        }
        return perMonth;
    }

    @Override
    public double totalProfit() {
        return this.repository.findAllByOrderStatus(OrderStatus.COMPLETED)
                .stream()
                .mapToDouble(Order::getTotal)
                .sum();
    }

    @Override
    public Map<String, Double> mapYearly(int year) {
        List<Double> perMonth = this.profitForYearPerMonths(year);
        Map<String, Double> totalPerMonth = new HashMap<>();
        int i = 0;
        for(String m: getMonths()){
            if(i!=12) {
                totalPerMonth.put(m, perMonth.get(i));
            }
            i++;
        }
        return totalPerMonth;
    }


}
