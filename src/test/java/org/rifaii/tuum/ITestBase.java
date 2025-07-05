package org.rifaii.tuum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rifaii.tuum.audit.DataChangeLog;
import org.rifaii.tuum.audit.DataChangeNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/*
 * Transactional here makes it so any data inserted by a test
 * is rolled back at the end of the test, this is ideal to keep
 * tests isolated and avoid side effects
 */
@Transactional
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ITestBase {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;

    @MockitoSpyBean protected DataChangeNotifier dataChangeNotifier;

    protected void assertChangesNotified(int numberOfChanges) {
        verify(dataChangeNotifier, times(numberOfChanges)).notify(any(DataChangeLog.class));
    }

}
