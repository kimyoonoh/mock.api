package kr.intube.apps.mockapi.component;

import aidt.gla.common.component.PropertySourceManager;
import aidt.gla.common.exception.ApiException;
import aidt.gla.common.exception.error.ApiErrorCode;
import aidt.gla.common.http.response.ResponseResult;
import aidt.gla.common.model.valueset.ValueSet;
import aidt.gla.common.tools.biz.Bool;
import aidt.gla.common.tools.biz.Checker;
import aidt.gla.common.tools.context.SpringContext;
import aidt.gla.common.utils.DateUtil;
import aidt.gla.common.utils.FileUtil;
import aidt.gla.common.utils.JSONUtil;
import aidt.gla.common.utils.StringUtil;
import kr.intube.apps.mockapi.common.code.DataColumnType;
import kr.intube.apps.mockapi.common.code.HttpResponseType;
import kr.intube.apps.mockapi.common.code.SheetName;
import kr.intube.apps.mockapi.common.google.drive.ExcelLoader;
import kr.intube.apps.mockapi.common.method.FuncRowToVO;
import kr.intube.apps.mockapi.common.model.DoubleRange;
import kr.intube.apps.mockapi.common.model.IntegerRange;
import kr.intube.apps.mockapi.common.model.MockRange;
import kr.intube.apps.mockapi.common.model.SheetVO;
import kr.intube.apps.mockapi.manage.api.vo.*;
import kr.intube.apps.mockapi.manage.dataset.vo.DataSetVO;
import kr.intube.apps.mockapi.manage.filter.vo.FilterVO;
import kr.intube.apps.mockapi.manage.generate.range.vo.RangeVO;
import kr.intube.apps.mockapi.manage.generate.valueset.vo.ValueSetVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@RestController
public class MockApiManager {
    private final static String REQUEST_MAPPING_BEAN_NAME = "requestMappingHandlerMapping";

    @Value("${google.drive.file-id:147B_F5J30hxmuLH3jFmtP8ZtUpbRCjNcfc8DDupZC14}")
    private String apiSheetFileId;

    RequestMappingHandlerMapping handlerMapping;

    Map<SheetName, FuncRowToVO> funcMakeVOMap;

    String excelFilePath;

    boolean isClient;

    @Autowired
    ExcelLoader excelLoader;

    public void init() {
        funcMakeVOMap = new LinkedHashMap<>();

        funcMakeVOMap.put(SheetName.API,        new ApiVO()::rowToVO);
        funcMakeVOMap.put(SheetName.REQ,        new ApiReqVO()::rowToVO);
        funcMakeVOMap.put(SheetName.RES,        new ApiResVO()::rowToVO);
        funcMakeVOMap.put(SheetName.RES_HEAD,   new ApiResHeaderVO()::rowToVO);
        funcMakeVOMap.put(SheetName.RES_COOKIE, new ApiResCookieVO()::rowToVO);
        funcMakeVOMap.put(SheetName.FILTER,     new FilterVO()::rowToVO);
        funcMakeVOMap.put(SheetName.DATASET,    new DataSetVO()::rowToVO);
        funcMakeVOMap.put(SheetName.RANGE,      new RangeVO()::rowToVO);
        funcMakeVOMap.put(SheetName.VALUESET,   new ValueSetVO()::rowToVO);
        funcMakeVOMap.put(SheetName.VAR,        new VariableVO()::rowToVO);

        this.excelFilePath = PropertySourceManager.getProperty("excel.file.path", "C:\\LMS-MOCKAPI.xlsx");
        this.isClient      = Bool.bool(PropertySourceManager.getProperty("client", "true"));
    }

    public boolean isBlankRow(LinkedHashMap<String, Object> row) {
        for (String key : row.keySet()) {
            String value = String.valueOf(row.get(key));

            if (!Checker.isBlank(value)) {
                if (Checker.notEq(value, "null")) return false;
            }
        }

        return true;
    }

    private static final String MARK_END_OF_ROW = "$$";
    public boolean isEndOfRow(LinkedHashMap<String, Object> row) {
        for (String key : row.keySet()) {
            String value = String.valueOf(row.get(key));

            if (Checker.startWith(value, MARK_END_OF_ROW)) return true;
        }

        return false;
    }

    private void loadSheetData(SheetName sheetName, List<LinkedHashMap<String, Object>> sheetData) {
        List<SheetVO> list = new ArrayList<>();

        for (LinkedHashMap<String, Object> row : sheetData) {
            if (isEndOfRow(row)) break;
            if (isBlankRow(row)) continue;

            log.info("{} Sheet : {}", sheetName, row);

            try {
                list.add(funcMakeVOMap.get(sheetName).make(row));
            } catch (Exception e) {
                log.debug("쉬트 데이터 오류 : {} / Exception {} ", row, e.getMessage());
            }
        }

        log.info("{} List: [{}]", sheetName.codeId, list.size());

        GlobalParams.put(sheetName.codeId, list);
    }

    public void loadMockData() throws IOException {
        log.info("Load Excel Workbook Data");

        Map<String, String> dataSheetName = new HashMap<>();

        LinkedHashMap<String, List<LinkedHashMap<String, Object>>> wb = this.isClient
                // 로컬 PC 에서는 구글 시트를 사용한다.
                ? excelLoader.loadGoogleSheetById(apiSheetFileId)
                // 서버에서는 서버 디렉토리내 엑셀 파일을 읽는다.
                : excelLoader.loadExcelFile(this.excelFilePath);

        for (String sheetName : wb.keySet()) {
            log.info("Sheet Name: {}", sheetName);

            SheetName SheetId = SheetName.getCode(sheetName);

            List<LinkedHashMap<String, Object>> sheetData = wb.get(sheetName);

            if (Checker.notEq(SheetId, SheetName.Unknown)) {
                // API, REQ, RES 등 고정 시트 처리
                loadSheetData(SheetId, sheetData);
            } else {
                // 고정 시트 외에는 데이터 시트다
                GlobalParams.put(sheetName, sheetData);

                dataSheetName.put(sheetName, sheetName);
            }
        }

        GlobalParams.put("dataSheetName", dataSheetName);
    }

    public Map<String, String> initVarMap() {
        // 로드된 시트 데이터를 기준으로 API 단위로 묶어서 재구성한다.
        List<VariableVO> varList = GlobalParams.get(SheetName.VAR.codeId);

        Map<String, String> varMap = new HashMap<>();

        for (VariableVO vo : varList) {
            varMap.put(vo.getId(), vo.getValue());
        }

        GlobalParams.put("VarMap", varMap);

        return varMap;
    }

    public Map<String, MockRange> initRangeMap() {
        List<RangeVO> ranges = GlobalParams.get(SheetName.RANGE.codeId);
        Map<String, MockRange> rangeMap = new HashMap<>();

        for (RangeVO range : ranges) {
            MockRange r = range.isReal() ? new DoubleRange() : new IntegerRange();

            r.setMax(range.getMaxValue());
            r.setMin(range.getMinValue());

            if (range.getStep() > 0) {
                r.setStep(range.getStep());
            } else if (range.getGap() != null) {
                r.setGap(range.getGap());
            } else if (range.getValueList().isEmpty()) {
                r.setGrade(range.getValueList());
            }

            if (range.getRatioList().length > 0) {
                r.setRatio(range.getRatioList());
            }

            rangeMap.put(range.getRangeId(), r);
        }

        GlobalParams.put("RangeMap", rangeMap);

        return rangeMap;
    }

    public Map<String, ValueSet<String>> initValueSetMap() {
        List<ValueSetVO> valueSets = GlobalParams.get(SheetName.VALUESET.codeId);
        Map<String, ValueSet<String>> valuesetMap = new HashMap<>();

        for (ValueSetVO vsVO : valueSets) {
            ValueSet<String> vs = vsVO.getRatioList().length > 0 ? new ValueSet<>(vsVO.getValueList(), vsVO.getRatioList()) : new ValueSet<>(vsVO.getValueList());

            valuesetMap.put(vsVO.getValueSetId(), vs);
        }

        GlobalParams.put("ValueSetMap", valuesetMap);

        return valuesetMap;
    }

    public Map<String, List<DataSetVO>> initDataSetMap() {
        List<DataSetVO> dataSets = GlobalParams.get(SheetName.DATASET.codeId);
        Map<String, List<DataSetVO>> datasetMap = new HashMap<>();

        for (DataSetVO dataset : dataSets) {
            String datasetId = dataset.getDataSetId();

            if (!datasetMap.containsKey(datasetId)) datasetMap.put(datasetId, new ArrayList<>());

            datasetMap.get(datasetId).add(dataset);
        }

        GlobalParams.put("DataSetMap", datasetMap);

        return datasetMap;
    }

    public String getVariable(String str) {
        String objectKey = str.startsWith("$") ? str.substring(1) : str;

        Map<String, String> varMap = GlobalParams.get("VarMap");

        return varMap.getOrDefault(objectKey, objectKey);
    }

    public Object getData(String nameOrValue) {
        String result = getVariable(nameOrValue);

        try {
            Map<String, Object> resultObj = JSONUtil.toMap(result);

            return resultObj == null ? result : resultObj;
        } catch (JsonParseException e) {
            return result;
        }
    }

    public void reconstMockData() {
        log.info("Reconstruction Mock Data ");

        // 공통 변수를 적용한다.
        initVarMap();

        // Range 객체를 적용한다.
        initRangeMap();

        // ValueSet 객체를 적용한다.
        initValueSetMap();

        // 데이터셋을 읽는다.
        initDataSetMap();

        List<ApiVO>          apis      = GlobalParams.get(SheetName.API.codeId);
        List<ApiReqVO>       requests  = GlobalParams.get(SheetName.REQ.codeId);
        List<ApiResVO>       responses = GlobalParams.get(SheetName.RES.codeId);
        List<ApiResHeaderVO> headers   = GlobalParams.get(SheetName.RES_HEAD.codeId);
        List<ApiResCookieVO> cookies   = GlobalParams.get(SheetName.RES_COOKIE.codeId);

        for (ApiVO api : apis) {
            // Bind Request
            for (ApiReqVO req : requests) {
                if (Checker.notEq(api.getApiId(), req.getApiId())) continue;

                api.getRequest().put(req.getParameterId(), req);
            }

            // Bind Response
            for (ApiResVO res : responses) {
                if (Checker.notEq(api.getApiId(), res.getApiId())) continue;

                api.getResponse().put(res.getResponseId(), res);

                switch (res.getResponseType()) {
                    case Data: api.setResData(res);
                        break;
                    case ErrorParameter: api.setResErrorParameter(res);
                        break;
                    case ErrorKey: api.setResErrorKey(res);
                }
            }
        }

        Map<String, List<DataSetVO>> datasetMap = GlobalParams.get("DataSetMap");

        // Bin Response attribte (Header, Cookie)
        for (ApiResVO res : responses) {
            // Global 설정이 최상단에 있으므로 중첩이 될 수 있도록 한다.
            for (ApiResHeaderVO header : headers) {
                if (Checker.isExclude(header.getResponseId(), "Global", res.getResponseId())) continue;

                res.getHeader().put(header.getHeaderId(), header);
            }

            // Global 설정이 최상단에 있으므로 중첩이 될 수 있도록 한다.
            for (ApiResCookieVO cookie : cookies) {
                if (Checker.isExclude(cookie.getResponseId(), "Global", res.getResponseId())) continue;

                res.getCookie().put(cookie.getCookieId(), cookie);
            }

            // 데이터 유형이면 데이터 타입으로 넣어야 한다.
            if (Checker.eq(res.getResponseType(), HttpResponseType.Data)) {
                String datasetName = (String) res.getResponseValue();

                log.info("Response result : {} {} {}", res.getResponseId(), res.getResponseType(), datasetName);

                // 데이터셋이 존재하면 데이터셋으로 찾는다.
                if (datasetMap.containsKey(datasetName)) {
                    res.setDataSetResultType(true);
                    res.setResponseValue(datasetMap.get(datasetName));
                } else {
                    // 아니면 즉치값이다.
                    res.setDataSetResultType(false);
                    res.setResponseValue(getData(datasetName));
                }
            }
        }
    }

    public void registRestApi() throws NoSuchMethodException {
        log.info("Regist Mock REST - API ");

        List<ApiVO> apis = GlobalParams.get(SheetName.API.codeId);

        for (ApiVO apiVO : apis) {
            registApi(apiVO);
        }
    }

    @GetMapping("/reload")
    public void reload() throws IOException, NoSuchMethodException {
        // 핸들링된 모든 API를 내린다.
        removeAllRequestMapping();

        GlobalParams.clear();

        loadMockData();

        reconstMockData();

        registRestApi();
    }

    private final static String SERVER_DOMAIN = "10.100.1.34";
    //private final static String SERVER_DOMAIN = "localhost";
    private final static int    SERVER_PORT = 8080;
    private final static String SERVER_RELOAD_ENDPOINT = "/receive/file";
    private final static String SERVER_URL = "http://%s:%s/api%s";

    private String getServerUrl() {
        return String.format(SERVER_URL, SERVER_DOMAIN, SERVER_PORT, SERVER_RELOAD_ENDPOINT);
    }

    public class Semaphore<T> {
        private AtomicBoolean status;
        private final String message;
        private Consumer<T> enableFunc;
        private Consumer<T> disableFunc;

        public Semaphore(boolean status, String message) {
            this.status  = new AtomicBoolean(status);
            this.message = message;
        }

        public Semaphore(boolean status, String message, Consumer<T> enableFunc, Consumer<T> disableFunc) {
            this.status  = new AtomicBoolean(status);
            this.message = message;

            this.enableFunc = enableFunc;
            this.disableFunc = disableFunc;
        }

        public void enable() {
            this.status = new AtomicBoolean(true);

            if (this.enableFunc != null) this.enableFunc.accept(null);
        }

        public void disable() {
            this.status = new AtomicBoolean(false);

            if (this.enableFunc != null) this.disableFunc.accept(null);
        }

        public boolean isEnable() {
            return this.status.get();
        }

        public String getMessage() {
            return this.message;
        }
    }

    private Semaphore reloading = new Semaphore(false, "갱신 처리중입니다. 잠시 기다려주십시요.");

    @GetMapping("/reload/server")
    public ResponseResult uploadFile() throws IOException, ApiException {
        ApiErrorCode.BadRequest.check(!isClient);
        // if () return ResponseResult.message("목업 서버에는 갱신 기능을 직접 호출할 수 없습니다.");

        if (reloading.isEnable()) return ResponseResult.message(reloading.getMessage());;

        reloading.enable();;

        // 구글 시트를 읽어서 InputStream으로 전환
        InputStream is = excelLoader.getGoolgleDriveFileInputStream(this.apiSheetFileId);

        sendFileBase64(getServerUrl(), FileUtil.read(is));

        reloading.disable();

        return ResponseResult.message("목업 서버로 구글 시트 파일이 전송되었습니다.");
    }

    // / reload/server 가 로컬 PC에서 호출되면 LDC 서버의 이 메소드가 호출된다.
    @PostMapping("/receive/file")
    public ResponseResult receiveFile(HttpServletRequest req) throws IOException, NoSuchMethodException {
        if (isClient) return ResponseResult.ok("Client mode cannot receive file!!", null);

        InputStream is = req.getInputStream();

        // 로컬 파일을 덮어쓴다.
        FileUtil.save(excelFilePath,  FileUtil.read(is));

        // 리로드한다.
        reload();

        return ResponseResult.ok("Receive Sheet file!!", null);
    }

    public void  sendFileBase64(String reloadUrl, byte [] fileContent) throws IOException {
            URL url = new URL(reloadUrl);

            // URL Connection 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // HTTP Request Method 설정 GET, POST, PUT ...
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(fileContent.length));

            conn.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(fileContent);
                wr.flush();
            }

            byte [] sendResult = FileUtil.read(conn.getInputStream());

            log.debug("Post Call return : {}", new String(sendResult, "UTF-8"));
    }

    public MockApiManager() {
        this.handlerMapping = getRequestMappingHandlerMapping();
    }

    Map<String, ApiVO> apiInfo = new HashMap<>();

    @Getter
    private class ReqMappInfo {
        RequestMappingInfo mapp;
        Method method;
        String apiId;

        public ReqMappInfo(RequestMappingInfo mapp) throws NoSuchMethodException {
            this.mapp   = mapp;
            this.method = MockApiManager.class.getDeclaredMethod("apiGateway", HttpServletRequest.class);
        }

        public void setApiId(String apiId) {
            this.apiId = apiId;
        }
    }

    private RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return SpringContext.getBean(REQUEST_MAPPING_BEAN_NAME, RequestMappingHandlerMapping.class);
    }

    public RequestMappingInfo getMapp(String mappUrl, String mappingName) {
        return getMapp(mappUrl, RequestMethod.GET, mappingName);
    }

    public RequestMappingInfo getMapp(String mappUrl, RequestMethod method, String mappingName) {
        RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
        options.setPatternParser(new PathPatternParser());

        return RequestMappingInfo
                .paths(mappUrl)
                .methods(method)
                .mappingName(mappingName)
                .options(options)
                .build();
    }

    static Map<String, ReqMappInfo> mappings = new HashMap<>();

    public void registApi(ApiVO apiVO) throws NoSuchMethodException {
        ReqMappInfo reqMappInfo = new ReqMappInfo(getMapp(apiVO.getEndPoint(), apiVO.getMethod().getMethod(), apiVO.getApiId()));
        reqMappInfo.setApiId(apiVO.getApiId());

        apiInfo.put(apiVO.getApiId(), apiVO);

        registRequestMapping(reqMappInfo);
    }

    public void registRequestMapping(String mappUrl, String mappingName) throws NoSuchMethodException {
        registRequestMapping(new ReqMappInfo(getMapp(mappUrl, mappingName)));
    }

    public void registRequestMapping(ReqMappInfo mappInfo) {
        log.info("Regist Mock API URL : {}", mappInfo.getMapp());

        mappings.put(mappInfo.apiId, mappInfo);

        handlerMapping.registerMapping(mappInfo.getMapp(), this, mappInfo.getMethod());
    }

    @GetMapping("/removeAll")
    public void removeAllRequestMapping() {
        for (String apiId : mappings.keySet()) {
            removeRequestMapping(apiId);
        }
    }

    private ReqMappInfo getMappInfo(String apiId) {
        return mappings.get(apiId);
    }

    @GetMapping("/remove/{apiId}")
    public void removeRequestMapping(@PathVariable String apiId) {
        ReqMappInfo mappInfo = getMappInfo(apiId);
        log.info("remove request Mapping Handler : {} {}", mappInfo.getMapp().getName(), mappInfo.getMethod().getName());

        handlerMapping.unregisterMapping(mappInfo.getMapp());
    }

    @GetMapping("/refresh/{apiId}")
    public void updateRequestMapping(@PathVariable String apiId) {
        ReqMappInfo mappInfo = getMappInfo(apiId);

        log.info("refresh request Mapping Handler : {} {}", mappInfo.getMapp().getName(), mappInfo.getMethod().getName());

        handlerMapping.unregisterMapping(mappInfo.getMapp());
        handlerMapping.registerMapping(mappInfo.getMapp(), this, mappInfo.getMethod());
    }

    private HandlerMethod handlerMethod(HttpServletRequest request) throws Exception {
        return (HandlerMethod) handlerMapping.getHandler(request).getHandler();
    }

    private RequestMappingInfo requestMapping(HttpServletRequest request) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        for(Map.Entry entry : handlerMethods.entrySet()) {
            if (Checker.isNotNull(((RequestMappingInfo) entry.getKey()).getMatchingCondition(request))) return (RequestMappingInfo) entry.getKey();
        }

        return null;
    }

    private Map<String, String> getPathVariable(HttpServletRequest request) throws Exception {
        PathContainer path = ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication();
        PathPattern pathPattern = requestMapping(request).getPathPatternsCondition().getFirstPattern();
        PathPattern.PathMatchInfo matchAndExtract = pathPattern.matchAndExtract(path);

        return matchAndExtract.getUriVariables();
    }

    public Map<String, String> bindParameterToMap(HttpServletRequest request) {
        return bindParameterToMap(request.getParameterMap());
    }

    public Map<String, String> bindParameterToMap(Map<String, String[]> requestParameters) {
        Map<String, String> bindParameterMap = new HashMap<>();

        if ((requestParameters == null) && (requestParameters.isEmpty())) return bindParameterMap;

        for (String parameter : requestParameters.keySet()) {
            String[] values = requestParameters.get(parameter);
            if (values.length > 1) {
                bindParameterMap.put(parameter, StringUtil.concat(values));
            } else {
                bindParameterMap.put(parameter, values[0]);
            }
        }

        return bindParameterMap;
    }

    public Map<String, String> getRequestHeader(HttpServletRequest request) {
        Map<String, String> requestHeaderMap = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            requestHeaderMap.put(headerName, request.getHeader(headerName));
        }

        return requestHeaderMap;
    }

    public Map<String, String> getRequestCookie(HttpServletRequest request) {
        Map<String, String> requestCookieMap = new HashMap<>();

        if (Checker.isNull(request.getCookies())) return requestCookieMap;

        for (Cookie cookie : request.getCookies()) {
            requestCookieMap.put(cookie.getName(), cookie.getValue());
        }

        return requestCookieMap;
    }

    public Map<String, String> getRequestBody(HttpServletRequest request) throws IOException {
        Map<String, String> requestBodyMap = new HashMap<>();

        String contentType = request.getHeader("Content-Type");

        byte [] bodyContent = request.getInputStream().readAllBytes();

        boolean isText = contentType.startsWith("text");
        boolean isJson = contentType.endsWith("json");
        boolean isForm = contentType.endsWith("www-form-urlencoded");

        if (isText) {
            String bodyText = new String(bodyContent, "UTF-8");

            // JSON 을 MAP 으로 만든다.
            if (isJson) return JSONUtil.toMap(bodyText);

            if (isForm) {
                // www-form-urlencoded 로 넘어올 경우에는 쿼리 스트링 형태이므로 문자열을 분해해서 파라미터를 만든다.
                for (String param : bodyText.split("&")) {
                    String[] token = param.split("=");

                    String key   = token[0];
                    String value = token.length > 1 ? token[1] : "";

                    if (requestBodyMap.containsKey(key)) {
                        // 배열이라면
                        String preValue = requestBodyMap.get(key);
                        requestBodyMap.put(key, preValue.concat(",").concat(value));
                    } else {
                        requestBodyMap.put(key, value);
                    }
                }
            }
        } else {
            // 바이너리 유형일 경우에는 처리할 껀던지가 없음
            log.error("Unsupported content type: {}", contentType);
        }

        //
        return requestBodyMap;
    }

    public boolean notExistRequestData(ApiReqVO reqVO, Map<String, String> map) {
        return reqVO.isRequireYn() && !map.containsKey(reqVO.getParameterId());
    }

    public Map<String, Object> getRequestParameterMap(HttpServletRequest request, Map<String, ApiReqVO> reqInfos) throws Exception {
        Map<String, String> requestPath   = getPathVariable(request);
        Map<String, String> requestQuery  = bindParameterToMap(request);
        Map<String, String> requestCookie = getRequestCookie(request);
        Map<String, String> requestHeader = getRequestHeader(request);
        Map<String, String> requestBody   = getRequestBody(request);

        Map<String, Object> requestParameter = new HashMap<>();

        String value = null;
        for (String reqId : reqInfos.keySet()) {
            ApiReqVO reqVO = reqInfos.get(reqId);

            switch (reqVO.getParamType()) {
                case Path:
                    ApiErrorCode.BadRequest.check(notExistRequestData(reqVO, requestPath));
                    value = requestPath.get(reqId);
                    requestParameter.put(reqId, Checker.isNull(value) ? reqVO.getDefaultValue() : value);
                    break;
                case Header:
                    ApiErrorCode.BadRequest.check(notExistRequestData(reqVO, requestHeader));
                    value = requestHeader.get(reqId);
                    requestParameter.put(reqId, Checker.isNull(value) ? reqVO.getDefaultValue() : value);
                    break;
                case Cookie:
                    ApiErrorCode.BadRequest.check(notExistRequestData(reqVO, requestCookie));
                    value = requestCookie.get(reqId);
                    requestParameter.put(reqId, Checker.isNull(value) ? reqVO.getDefaultValue() : value);
                    break;
                case Query:
                    ApiErrorCode.BadRequest.check(notExistRequestData(reqVO, requestQuery));
                    value = requestQuery.get(reqId);
                    requestParameter.put(reqId, Checker.isNull(value) ? reqVO.getDefaultValue() : value);
                    break;
                case Body:
                    ApiErrorCode.BadRequest.check(notExistRequestData(reqVO, requestBody));
                    value = requestBody.get(reqId);
                    requestParameter.put(reqId, Checker.isNull(value) ? reqVO.getDefaultValue() : value);
                default:

            }
        }

        return requestParameter;
    }

    public Map<String, Object> copyMap(Map<String, Object> srcMap) {
        Map<String, Object> targetMap = new HashMap<>();

        for (String key : srcMap.keySet()) {
            targetMap.put(key, srcMap.get(key));
        }

        return targetMap;
    }

    public List<Map<String, Object>> getRowData(List<DataSetVO> datasetInfos, Map<String, Object> inputParams) throws ApiException {
        List<Map<String, Object>> result = new ArrayList<>();

        String datasetId = datasetInfos.get(0).getDataSetId();

        Map<String, List<DataSetVO>> datasetMap = GlobalParams.get("DataSetMap");
        Map<String, MockRange> rangeMap         = GlobalParams.get("RangeMap");
        Map<String, ValueSet<String>> valuesetMap       = GlobalParams.get("ValueSetMap");
        //Map<String, String> varMap              = GlobalParams.get("VarMap");

        List<LinkedHashMap<String, Object>> rows = GlobalParams.get(datasetId);

        ApiErrorCode.NotFound.check(rows.isEmpty());

        for (LinkedHashMap<String, Object> row : rows) {
            Map<String, Object> rowData = new HashMap<>();

            Map<String, Object> searchData = copyMap(inputParams);

            boolean isCorrect = true;
            for (DataSetVO column : datasetInfos) {
                String columnId = column.getColumnId();
                String mappId   = column.getMappingId();

                Object value = row.get(columnId);
                if (Checker.isNull(value)) continue;

                Object key = inputParams.get(columnId);

                // 매핑 ID가 존재하면 검색 키값에 매핑 키 값을 넣는다.
                if (Checker.isNotEmpty(mappId)) {
                    searchData.put(columnId, row.get(mappId));
                }

                String valueKey = value.toString();

                boolean isObject = valueKey.startsWith("$");
                String objectKey = isObject ? valueKey.substring(1) : valueKey;

                switch (column.getGenType()) {
                case Literal:
                    value = column.getDataType().getValue(getData(objectKey));
                    break;
                case DataSet :
                    if (isObject) {
                        if (Checker.eq(column.getDataType(), DataColumnType.List)) {
                            value = getRowData(datasetMap.get(objectKey), searchData);
                        } else {
                            List<Map<String, Object>> dataRow = getRowData(datasetMap.get(objectKey), searchData);

                            value = dataRow.isEmpty() ? dataRow.get(0) : "{}";
                        }
                    }
                    break;
                case Range     : if (isObject) value = rangeMap.get(objectKey).sample();
                    break;
                case ValueSet  : if (isObject) value = valuesetMap.get(objectKey).sample();
                    break;
                case Parameter : if (isObject) value = inputParams.get(objectKey);
                    break;
                case Variable:
                    if (isObject) {
                        switch (objectKey.toUpperCase()) {
                            case "TIMESTAMP" : value = System.currentTimeMillis();
                                break;
                            case "DATETIMEMIL" : value = DateUtil.format(DateUtil.Pattern.DECO_YMDHMST);
                                break;
                            case "TODAY" :
                            case "DATETIME" : value = DateUtil.format(DateUtil.Pattern.DECO_YMDHMS);
                                break;
                            case "DATE"  : value = DateUtil.format(DateUtil.Pattern.DECO_YMD);
                                break;
                            case "TIME"  : value = DateUtil.format(DateUtil.Pattern.DECO_YMDHMS).substring(11);
                                break;
                            case "UUID"  : value = UUID.randomUUID().toString();
                                break;
                            default : value = getData(objectKey);
                        }
                    }
                }

                // 입력된 키값 파라미터가 존재할때
                if (Checker.isNotEmpty(key) ) {
                    // 키값이 맞지 않는다면 데이터를 반환하지 않는다.
                    if (Checker.notEq(key, value)) isCorrect = false;
                }

                rowData.put(column.getColumnId(), Checker.isNull(value) ? column.getDefaultValue() : value);
            }

            if (isCorrect) result.add(rowData);
        }

        return result;
    }

    private final static String RES_MESSAGE = "MOCKUP API [%s] %s : %s";

    public ResponseResult apiGateway(HttpServletRequest request) throws Exception {
        RequestMappingInfo rm = requestMapping(request);

        ApiErrorCode.BadRequest.check(Checker.isNull(rm));

        ApiVO apiVO = apiInfo.get(rm.getName());

        log.info("Api Gateway : {} {}", request.getRequestURL(), rm.getName());

        Map<String, Object> inputParams = getRequestParameterMap(request, apiVO.getRequest());

        Object responseValue = apiVO.getResData().getResponseValue();

        String resMessage = String.format(RES_MESSAGE, apiVO.getApiId(), apiVO.getEndPoint(), apiVO.getApiName());

        // 데이터셋 형태의 데이터가 아니라면 바로 리턴한다.
        if (!apiVO.getResData().isDataSetResultType()) return ResponseResult.data(responseValue);

        // 데이터셋이면 데이터시트에서 RowData를 검색하여 반환한다.
        List<DataSetVO> datasetInfos = (List<DataSetVO>) responseValue;

        // Dataset 이 없다면.. Response Result 로 등록된 값을 바로 리턴한다.
        if (datasetInfos.isEmpty()) return ResponseResult.build(HttpStatus.OK, resMessage, apiVO.getResData().getResponseValue());

        // 결과값이 컬렉션이라면
        if (apiVO.getResData().isCollectionResult()) {
            // 베열 형태로 반환함
            return ResponseResult.build(HttpStatus.OK, resMessage, getRowData(datasetInfos, inputParams));
        } else {
            // 컬렉션이 아니라면
            List<Map<String, Object>> result = getRowData(datasetInfos, inputParams);

            // 검색
            if (result.isEmpty()) {
                // 검색된 데이터가 없다면
                return ResponseResult.build(HttpStatus.OK, resMessage, new HashMap<String, Object>());
            } else {
                // 검색된 값이 있다면 0번째 요소를 반환
                return ResponseResult.build(HttpStatus.OK, resMessage, result.get(0));
            }
        }
    }
}
