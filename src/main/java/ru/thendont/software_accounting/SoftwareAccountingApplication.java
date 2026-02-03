package ru.thendont.software_accounting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.thendont.software_accounting.util.ConstantStrings;

@SpringBootApplication()
public class SoftwareAccountingApplication {

	private static final Logger logger = LogManager.getLogger(ConstantStrings.LOGGER_NAME);

	public static void main(String[] args) {
		logger.info("=== НАЧАЛО ЗАПУСКА ПРИЛОЖЕНИЯ ===");
		SpringApplication.run(SoftwareAccountingApplication.class, args);
		logger.info("=== ПРИЛОЖЕНИЕ УСПЕШНО ЗАПУЩЕНО ===");
	}
}