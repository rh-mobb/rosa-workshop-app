package com.redhat.developers.microsweeper.service;

import com.redhat.developers.microsweeper.model.Score;

import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ScoreboardService extends AbstractService {

    @Inject
    DynamoDbClient dynamoDB;

    public ScoreboardService () {

    	System.setProperty(SdkSystemSetting.SYNC_HTTP_SERVICE_IMPL.property(), "software.amazon.awssdk.http.apache.ApacheSdkHttpService");

    	dynamoDB = DynamoDbClient.builder()
        	.credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
		.httpClient(ApacheHttpClient.create())
        	.build();
    }

    public List<Score> getScoreboard() {
        return dynamoDB.scanPaginator(scanRequest()).items().stream()
                .map(Score::from)
                .collect(Collectors.toList());
    }

    public void addScore(Score score) {
        dynamoDB.putItem(putRequest(score));
    }

}