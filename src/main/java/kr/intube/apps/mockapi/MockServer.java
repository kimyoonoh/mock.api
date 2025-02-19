package kr.intube.apps.mockapi;

import aidt.gla.common.resource.ResourceUtil;
import aidt.gla.common.tools.password.RandomMerged;
import aidt.gla.common.utils.FileUtil;
import kr.intube.apps.mockapi.component.MockApiManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@Component("mockServer")
public class MockServer implements ApplicationRunner {
    MockApiManager apiManager;

    public MockServer(MockApiManager apiManager) {
        this.apiManager = apiManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //aaa();

        this.apiManager.init();

        this.apiManager.loadMockData();

        this.apiManager.reconstMockData();

        this.apiManager.registRestApi();
    }
    /*
    public void aaa() throws IOException {
        RandomMerged rm = new RandomMerged();

        String jsonText = FileUtil.readText("D:\\aidt_workshop\\api-project-108305436670-0be9e88fadf8.json");

        String endText = rm.mergeEncrypt(jsonText);

        FileUtil.save("D:\\aidt_workshop\\aidt-adv-lms-mock-server\\src\\main\\resources\\API-CREDENTION.data", endText);
    }
    */
}
