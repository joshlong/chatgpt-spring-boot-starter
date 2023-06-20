package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.ChatFunction;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mvnsearch.chatgpt.model.function.GPTFunctionUtils.extractFunctions;

/**
 * Maintains references to all the functions used as callbacks.
 *
 * Also registers required hints so that those functions work in a GraalVM native image
 * context.
 */
class GPTFunctionsRegistry implements BeanPostProcessor, InitializingBean, BeanRegistrationAotProcessor {

	private static final Logger log = LoggerFactory.getLogger(GPTFunctionsRegistry.class);

	private final Map<String, ChatGPTJavaFunction> functions = new ConcurrentHashMap<>();

	private final Map<String, ChatGPTJavaFunction> javaFunctions = new ConcurrentHashMap<>();

	private final Map<String, ChatFunction> chatFunctions = new ConcurrentHashMap<>();

	@Override
	public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
		final String beanName = registeredBean.getBeanName();
		final Class<?> beanClazz = registeredBean.getBeanClass();
		try {

			final Map<String, ChatGPTJavaFunction> discovered = extractFunctions(beanClazz);

			final MemberCategory[] memberCategories = MemberCategory.values();

			if (discovered.isEmpty())
				return null;

			return (generationContext, beanRegistrationCode) -> {
				var reflection = generationContext.getRuntimeHints().reflection();
				reflection.registerType(beanClazz, memberCategories);
				log.info("registering AOT hints for bean {} with class {}", beanName, beanClazz.getName());
			};

		} //
		catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("no functions to register for bean {}", registeredBean.getBeanName());
			}
		}

		return null;
	}

	@Override
	public boolean isBeanExcludedFromAotProcessing() {
		return false;
	}

	private static <K, T> boolean hasDuplicates(Map<K, T> a, Map<K, T> b) {
		final Map<K, AtomicInteger> freq = new HashMap<>();
		for (final Set<K> kk : Set.of(a.keySet(), b.keySet())) {
			for (K k : kk) {
				freq.putIfAbsent(k, new AtomicInteger(0));
				freq.get(k).incrementAndGet();
			}
		}
		return freq.values().stream().anyMatch(ai -> ai.get() > 1);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		try {
			final Map<String, ChatGPTJavaFunction> map = extractFunctions(bean.getClass());
			map.forEach((k, v) -> v.setTarget(bean));
			Assert.state(!hasDuplicates(this.functions, map), "there should not be any duplicate functions!");
			this.functions.putAll(map);

		} //
		catch (Exception e) {
			log.warn("could not extract functions for bean {} ", beanName);
		}
		return bean;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.functions.forEach((functionName, func) -> {
			this.javaFunctions.put(functionName, func);
			this.chatFunctions.put(functionName, func.toChatFunction());
		});
		this.functions.clear();
	}

	public ChatGPTJavaFunction getChatGPTJavaFunction(String functionName) {
		return this.javaFunctions.get(functionName);
	}

	public ChatFunction getChatFunction(String functionName) {
		return this.chatFunctions.get(functionName);
	}

}
