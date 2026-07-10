package com.naisa.stock_sync_scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StockSyncSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockSyncSchedulerApplication.class, args);
	}

}
