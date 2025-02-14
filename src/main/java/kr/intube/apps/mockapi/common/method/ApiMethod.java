package kr.intube.apps.mockapi.common.method;

import aidt.gla.common.http.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface ApiMethod {
    ResponseResult apiGateway(HttpServletRequest request, HttpServletResponse response) throws Exception;
}