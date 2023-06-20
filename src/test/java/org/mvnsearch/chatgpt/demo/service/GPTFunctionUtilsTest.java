package org.mvnsearch.chatgpt.demo.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;

import java.util.Map;

class GPTFunctionUtilsTest {

	@Test
	void testExtractFunctions() throws Exception {
		final Map<String, ChatGPTJavaFunction> functions = GPTFunctionUtils.extractFunctions(GPTFunctions.class);
		Assertions.assertThat(functions).hasSizeGreaterThan(1);
		for (Map.Entry<String, ChatGPTJavaFunction> entry : functions.entrySet()) {
			final ChatGPTJavaFunction javaFunction = entry.getValue();
			System.out.println(javaFunction.getJavaMethod().getName());
		}
	}

}
