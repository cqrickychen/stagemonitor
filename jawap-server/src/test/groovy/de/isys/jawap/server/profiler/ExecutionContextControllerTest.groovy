package de.isys.jawap.server.profiler

import de.isys.jawap.entities.MeasurementSession
import de.isys.jawap.server.TestData
import de.isys.jawap.server.WebAppJpaTest
import org.junit.Before
import org.junit.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext

import javax.inject.Inject

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

@WebAppJpaTest
public class ExecutionContextControllerTest {

	@Inject WebApplicationContext wac;
	@Inject TestData testData
	private MockMvc mvc;

	@Before
	public void setup() {
		this.mvc = webAppContextSetup(this.wac).build();
		MeasurementSession session = testData.addMeasurementSession('app 1')
		testData.addHttpRequestContext(session, "bla")
		testData.addHttpRequestContext(session, "blubb")

		session = testData.addMeasurementSession('app 2')
		testData.addHttpRequestContext(session, "bla")
		testData.addHttpRequestContext(session, "blubb")
	}

	@Test
	void testSearchApplicationNameOnly() {
		def response = mvc.perform(
				get("/executionContexts/search")
						.param("name", "bla"))
				.andExpect(status().isOk())
				.andReturn().response.contentAsString.json
		assert response.aaData.size() == 2
	}

	@Test
	void testSearchApplicationNameApp() {
		def response = mvc.perform(
				get("/executionContexts/search")
						.param("name", "bla")
						.param("application", "app_1")
						.param("host", "*"))
				.andExpect(status().isOk())
				.andReturn().response.contentAsString.json
		assert response.aaData.size() == 1
	}

	@Test
	void testSearchApplicationNameAppHostEnv() {
		def response = mvc.perform(
				get("/executionContexts/search")
						.param("name", "bla")
						.param("application", "app_1")
						.param("host", "localhorst")
						.param("instance", "test"))
				.andExpect(status().isOk())
				.andReturn().response.contentAsString.json
		assert response.aaData.size() == 1
	}

}
