package kr.intube.apps.mockapi;

import kr.intube.apps.mockapi.component.MockApiManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component("mockServer")
public class MockServer implements ApplicationRunner {
    MockApiManager apiManager;

    public MockServer(MockApiManager apiManager) {
        this.apiManager = apiManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.apiManager.init();

        this.apiManager.loadMockData();

        this.apiManager.reconstMockData();

        this.apiManager.registRestApi();
    }
}
