package com.example.demo.connector;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.demo.vo.CrnInfo;
import com.example.demo.vo.RespDetails;

@Component
public class AccountServiceConnector {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	RestTemplate accountServiceRestTemplate;

	@Value("${accountService.read.timeout:5}")
	public int accountServiceReadTimeout;

	@Value("${accountService.connection.timeout:100}")
	public int accountServiceConnectionTimeout;

	@Value("${accountService.connection.request.timeout:10}")
	public int accountServiceConnectionRequestTimeout;
//
	@Value("${accountService.url}")
	public String accountServiceUrl;

	@PostConstruct
	public void init() {

		TrustStrategy acceptingTrustStrategy = (chain, authType) -> true;
		SSLConnectionSocketFactory sslsf = null;
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
			sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
			throw new RuntimeException(e);
	}

		final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", new PlainConnectionSocketFactory()).register("https", sslsf).build();

		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		cm.setMaxTotal(100);
		HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).build();
		HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory();
		rf.setHttpClient(httpClient);
		rf.setReadTimeout(this.accountServiceReadTimeout * 1000);
		rf.setConnectTimeout(this.accountServiceConnectionTimeout * 1000);
		rf.setConnectionRequestTimeout(this.accountServiceConnectionRequestTimeout * 1000);
		this.accountServiceRestTemplate = new RestTemplate(rf);
	}

	public RespDetails send(String crnNo) {
		try {
			RespDetails respDetails = accountServiceRestTemplate.getForObject(accountServiceUrl + "/" + crnNo,RespDetails.class);
			return respDetails;
		} catch (Exception e) {
			logger.error("error", e);
		}
		return null;
	}

}