package com.example.demo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DemoApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
		Human h1 = applicationContext.getBean(Human.class);
		System.out.println(h1);
	}
}


class Human {
	private String name;

	public Human(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Human{" +
				"name='" + name + '\'' +
				'}';
	}
}

class HumanWithLogging extends Human {

	public HumanWithLogging(Human h) {
		super(h.getName());
	}

	@Override
	public String toString() {
		System.out.println("Method to String was called");
		return super.toString();
	}
}

class HumanBeanPostProcessor implements BeanPostProcessor {
	private String shouldBeWrapped;

	@Override
	public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
		if (o instanceof Human) {
			shouldBeWrapped = s;
		}
		return o;
	}

	@Override
	public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
		if (Objects.equals(shouldBeWrapped, s)) {
			return new HumanWithLogging((Human)o);
		}
		return o;
	}
}

@Configuration
class Config {

	@Bean
	public HumanBeanPostProcessor humanBeanPostProcessor() {
		return new HumanBeanPostProcessor();
	}

	@Bean
	public Human h1(@Qualifier("h1Name") String a) {
		return new Human(a);
	}

	@Bean
	public String h1Name() {
		return "Human 1";
	}
}