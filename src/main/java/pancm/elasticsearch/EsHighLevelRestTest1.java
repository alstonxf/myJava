package pancm.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ScrollableHitSource;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @Title: EsHighLevelRestTest1
 * @Description: Java High Level REST Client Es?????????????????????????????? (??????CRUD??????) ??????????????????:
 *               https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html
 * @since jdk 1.8
 * @Version:1.0.0
 * @author pancm
 * @date 2019???3???5???
 */
public class EsHighLevelRestTest1 {

	private static String elasticIp = "192.169.0.23";
	private static int elasticPort = 9200;

	private static Logger logger = LoggerFactory.getLogger(EsHighLevelRestTest1.class);

	private static RestHighLevelClient client = null;

	public static void main(String[] args) {
		try {
			init();
			createIndex();
			insert();
			queryById();
			exists();
			update();
//			deleteByQuery();
//			deleteIndex();
//			delete();
//			bulk();
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void insert() throws IOException {
		String index = "test1";
		String type = "_doc";
		// ????????????
		String id = "1";
		IndexRequest request = new IndexRequest(index, type, id);
		/*
		 * ????????????????????????jsonString????????????
		 */
		// json
		String jsonString = "{" + "\"uid\":\"1234\","+ "\"phone\":\"12345678909\","+ "\"msgcode\":\"1\"," + "\"sendtime\":\"2019-03-14 01:57:04\","
				+ "\"message\":\"xuwujing study Elasticsearch\"" + "}";
		request.source(jsonString, XContentType.JSON);

		/*
		 * ????????????????????????map??????,?????????????????????json?????????
		 */
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("uid", 1234);
		jsonMap.put("phone", 12345678909L);
		jsonMap.put("msgcode", 1);
		jsonMap.put("sendtime", "2019-03-14 01:57:04");
		jsonMap.put("message", "xuwujing study Elasticsearch");
		request.source(jsonMap);

		/*
		 * ??????????????? : ??????XContentBuilder??????????????????
		 */

		XContentBuilder builder = XContentFactory.jsonBuilder();
		builder.startObject();
		{
			builder.field("uid", 1234);
			builder.field("phone", 12345678909L);
			builder.field("msgcode", 1);
			builder.timeField("sendtime", "2019-03-14 01:57:04");
			builder.field("message", "xuwujing study Elasticsearch");
		}
		builder.endObject();
		request.source(builder);

		IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

		//?????????200????????????????????????????????????
		if(200 == indexResponse.status().getStatus()){

		}

		// ???????????????????????????
		String index1 = indexResponse.getIndex();
		String type1 = indexResponse.getType();
		String id1 = indexResponse.getId();
		long version = indexResponse.getVersion();
		// ???????????????/??????????????????
		if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {

		} else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {

		}
		ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
		if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

		}
		if (shardInfo.getFailed() > 0) {
			for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
				String reason = failure.reason();
			}
		}
	}

	/*
	 * ???????????????
	 */
	private static void init() {
		
		client = new RestHighLevelClient(RestClient.builder(new HttpHost(elasticIp, elasticPort)));

	}

	/*
	 * ????????????
	 */
	private static void close() {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ????????????
	 * 
	 * @throws IOException
	 */
	private static void createIndex() throws IOException {

		// ??????
		String type = "_doc";
		String index = "test1";
		// setting ??????
		Map<String, Object> setmapping = new HashMap<>();
		// ??????????????????????????????????????????
		setmapping.put("number_of_shards", 10);
		setmapping.put("number_of_replicas", 1);
		setmapping.put("refresh_interval", "5s");
		Map<String, Object> keyword = new HashMap<>();
		//????????????
		keyword.put("type", "keyword");
		Map<String, Object> lon = new HashMap<>();
		//????????????
		lon.put("type", "long");
		Map<String, Object> date = new HashMap<>();
		//????????????
		date.put("type", "date");
		date.put("format", "yyyy-MM-dd HH:mm:ss");

		Map<String, Object> jsonMap2 = new HashMap<>();
		Map<String, Object> properties = new HashMap<>();
		//????????????message??????
		properties.put("uid", lon);
		properties.put("phone", lon);
		properties.put("msgcode", lon);
		properties.put("message", keyword);
		properties.put("sendtime", date);
		Map<String, Object> mapping = new HashMap<>();
		mapping.put("properties", properties);
		jsonMap2.put(type, mapping);

		GetIndexRequest getRequest = new GetIndexRequest();
		getRequest.indices(index);
		getRequest.types(type);
		getRequest.local(false);
		getRequest.humanReadable(true);
		boolean exists2 = client.indices().exists(getRequest, RequestOptions.DEFAULT);
		//???????????????????????????
		if(exists2) {
			System.out.println(index+"?????????????????????!");
			return;
		}
		// ???????????????
		CreateIndexRequest request = new CreateIndexRequest(index);
		try {
			// ??????????????????
			request.settings(setmapping);
			//??????mapping??????
			request.mapping(type, jsonMap2);
			//????????????
			request.alias(new Alias("pancm_alias"));
			CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
			boolean falg = createIndexResponse.isAcknowledged();
			if(falg){
				System.out.println("???????????????:"+index+"?????????" );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/**
	 * ????????????
	 *
	 * @throws IOException
	 */
	private static void deleteIndex() throws IOException {
		String index = "userindex";
		DeleteIndexRequest  request = new DeleteIndexRequest(index);
		// ????????????
		client.indices().delete(request,RequestOptions.DEFAULT);
		System.out.println("????????????????????????"+index);

	}

	/**
	 * ????????????
	 * 
	 * @throws IOException
	 */
	private static void queryById() {
		String type = "_doc";
		String index = "test1";
		// ????????????
		String id = "1";
		// ??????????????????
		GetRequest getRequest = new GetRequest(index, type, id);

		GetResponse getResponse = null;
		try {
			getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ElasticsearchException e) {
			// ????????????????????????
			if (e.status() == RestStatus.NOT_FOUND) {
				System.out.println("????????????????????????" + index);
			}

		}
		// ?????????????????????????????????????????????
		if (getResponse.isExists()) {
			long version = getResponse.getVersion();
			String sourceAsString = getResponse.getSourceAsString();
			Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
			byte[] sourceAsBytes = getResponse.getSourceAsBytes();
			System.out.println("??????????????????String:" + sourceAsString);
			System.out.println("??????????????????Map:" + sourceAsMap);
		} else {
			System.out.println("????????????????????????");
		}
	}

	/**
	 * ????????????
	 * 
	 * @throws IOException
	 */
	private static void exists() throws IOException {
		String type = "_doc";
		String index = "test1";
		// ????????????
		String id = "1";
		// ??????????????????
		GetRequest getRequest = new GetRequest(index, type, id);

		
		boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);

		ActionListener<Boolean> listener = new ActionListener<Boolean>() {
			@Override
			public void onResponse(Boolean exists) {
				System.out.println("==" + exists);
			}

			@Override
			public void onFailure(Exception e) {
				System.out.println("??????????????????" + e.getMessage());
			}
		};
		// ??????????????????
//		client.existsAsync(getRequest, RequestOptions.DEFAULT, listener);

		System.out.println("?????????????????????" + exists);
	}

	/**
	 * ????????????
	 * 
	 * @throws IOException
	 */
	private static void update() throws IOException {
		String type = "_doc";
		String index = "test1";
		// ????????????
		String id = "1";
		UpdateRequest upateRequest = new UpdateRequest();
		upateRequest.id(id);
		upateRequest.index(index);
		upateRequest.type(type);

		// ??????????????????Map??????????????????????????????
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("uid", 12345);
		jsonMap.put("phone", 123456789019L);
		jsonMap.put("msgcode", 2);
		jsonMap.put("sendtime", "2019-03-14 01:57:04");
		jsonMap.put("message", "xuwujing study Elasticsearch");
		upateRequest.doc(jsonMap);
		// upsert ?????????????????????????????????????????????????????????
		upateRequest.docAsUpsert(true);
		client.update(upateRequest, RequestOptions.DEFAULT);
		System.out.println("???????????????");

	}



	/**
	 * ????????????????????????
	 *
	 * @throws IOException
	 */
	private static void updateByQuery() throws IOException {
		String type = "_doc";
		String index = "test1";
		//
		UpdateByQueryRequest request = new UpdateByQueryRequest(index,type);
		// ??????????????????
		request.setQuery(new TermQueryBuilder("user", "pancm"));
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

		// ???????????????????????????
		request.setSize(10);
		// ?????????????????????????????????????????????1000
		request.setBatchSize(100);
		//??????????????????
		request.setTimeout(TimeValue.timeValueMinutes(2));
		//????????????
		request.setIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);

		// ????????????
		BulkByScrollResponse bulkResponse = client.updateByQuery(request, RequestOptions.DEFAULT);

		// ????????????
//		client.updateByQueryAsync(request, RequestOptions.DEFAULT, listener);

		// ????????????
		TimeValue timeTaken = bulkResponse.getTook();
		boolean timedOut = bulkResponse.isTimedOut();
		long totalDocs = bulkResponse.getTotal();
		long updatedDocs = bulkResponse.getUpdated();
		long deletedDocs = bulkResponse.getDeleted();
		long batches = bulkResponse.getBatches();
		long noops = bulkResponse.getNoops();
		long versionConflicts = bulkResponse.getVersionConflicts();
		long bulkRetries = bulkResponse.getBulkRetries();
		long searchRetries = bulkResponse.getSearchRetries();
		TimeValue throttledMillis = bulkResponse.getStatus().getThrottled();
		TimeValue throttledUntilMillis = bulkResponse.getStatus().getThrottledUntil();
		List<ScrollableHitSource.SearchFailure> searchFailures = bulkResponse.getSearchFailures();
		List<BulkItemResponse.Failure> bulkFailures = bulkResponse.getBulkFailures();
		System.out.println("???????????????????????????:" + timeTaken.getMillis() + " ??????????????????:" + totalDocs + ",?????????:" + updatedDocs);

	}

	/**
	 * ??????
	 * 
	 * @throws IOException
	 * 
	 */
	private static void delete() throws IOException {

		String type = "_doc";
		String index = "test1";
		// ????????????
		String id = "1";
		DeleteRequest deleteRequest = new DeleteRequest();
		deleteRequest.id(id);
		deleteRequest.index(index);
		deleteRequest.type(type);
		// ??????????????????
		deleteRequest.timeout(TimeValue.timeValueMinutes(2));
		// ??????????????????"wait_for"
		// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		deleteRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
		// ????????????
		DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);

		/*
		 * ??????????????????
		 */

		// ????????????
		ActionListener<DeleteResponse> listener = new ActionListener<DeleteResponse>() {
			@Override
			public void onResponse(DeleteResponse deleteResponse) {
				System.out.println("??????:" + deleteResponse);
			}

			@Override
			public void onFailure(Exception e) {
				System.out.println("??????????????????:" + e.getMessage());
			}
		};

		// ????????????
//		 client.deleteAsync(deleteRequest, RequestOptions.DEFAULT, listener);

		ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
		// ?????????????????????????????????????????????????????????,??????????????????????????????????????????
		if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
			System.out.println("???????????????????????????:" + shardInfo.getTotal());
			System.out.println("???????????????????????????:" + shardInfo.getSuccessful());
		}

		if (shardInfo.getFailed() > 0) {
			for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
				String reason = failure.reason();
			}
		}
		System.out.println("????????????!");
	}


	/**
	 * ????????????????????????
	 *
	 * @throws IOException
	 */
	private static void deleteByQuery() throws IOException {
		String type = "_doc";
		String index = "test1";
		DeleteByQueryRequest request = new DeleteByQueryRequest(index,type);
		// ??????????????????
		request.setQuery(QueryBuilders.termQuery("uid",1234));
		// ????????????
		BulkByScrollResponse bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);

		// ????????????
//		client.updateByQueryAsync(request, RequestOptions.DEFAULT, listener);

		// ????????????
		TimeValue timeTaken = bulkResponse.getTook();
		boolean timedOut = bulkResponse.isTimedOut();
		long totalDocs = bulkResponse.getTotal();
		long updatedDocs = bulkResponse.getUpdated();
		long deletedDocs = bulkResponse.getDeleted();
		long batches = bulkResponse.getBatches();
		long noops = bulkResponse.getNoops();
		long versionConflicts = bulkResponse.getVersionConflicts();
		long bulkRetries = bulkResponse.getBulkRetries();
		long searchRetries = bulkResponse.getSearchRetries();
		TimeValue throttledMillis = bulkResponse.getStatus().getThrottled();
		TimeValue throttledUntilMillis = bulkResponse.getStatus().getThrottledUntil();
		List<ScrollableHitSource.SearchFailure> searchFailures = bulkResponse.getSearchFailures();
		List<BulkItemResponse.Failure> bulkFailures = bulkResponse.getBulkFailures();
		System.out.println("???????????????????????????:" + timeTaken.getMillis() + " ??????????????????:" + totalDocs + ",?????????:" + updatedDocs);

	}

	/**
	 * ??????????????????
	 * 
	 * @throws InterruptedException
	 */
	private static void bulk() throws IOException, InterruptedException {
		String index = "estest";
		String type = "estest";

		BulkRequest request = new BulkRequest();
		// ????????????,?????????????????????
		request.add(new IndexRequest(index, type, "1").source(XContentType.JSON, "field", "foo"));
		request.add(new IndexRequest(index, type, "2").source(XContentType.JSON, "field", "bar"));
		request.add(new IndexRequest(index, type, "3").source(XContentType.JSON, "field", "baz"));

		// ??????????????????/??????/?????? ??????
		//docAsUpsert ???true??????????????????????????????????????????false?????????????????????????????????
		request.add(new UpdateRequest(index, type, "2").doc(XContentType.JSON, "field", "test").docAsUpsert(true));
		request.add(new DeleteRequest(index, type, "3"));
		request.add(new IndexRequest(index, type, "4").source(XContentType.JSON, "field", "baz"));

		BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);


		ActionListener<BulkResponse> listener3 = new ActionListener<BulkResponse>() {
			@Override
			public void onResponse(BulkResponse response) {
				System.out.println("===="+response.buildFailureMessage());
			}

			@Override
			public void onFailure(Exception e) {
				System.out.println("====---"+e.getMessage());
			}
		};

		client.bulkAsync(request, RequestOptions.DEFAULT,listener3);

		// ??????????????????????????????????????????????????? true???????????????????????????
		if (bulkResponse.hasFailures()) {
			System.out.println("?????????????????????!");
		}

		// ?????????????????????????????????????????????????????????????????????
		for (BulkItemResponse bulkItemResponse : bulkResponse) {
			DocWriteResponse itemResponse = bulkItemResponse.getResponse();

			// ???????????????????????????
			if (bulkItemResponse.isFailed()) {
				BulkItemResponse.Failure failure = bulkItemResponse.getFailure();

			}

			if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
					|| bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
				IndexResponse indexResponse = (IndexResponse) itemResponse;
				System.out.println("????????????!"+indexResponse.toString());

			} else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
				UpdateResponse updateResponse = (UpdateResponse) itemResponse;
				System.out.println("????????????!"+updateResponse.toString());

			} else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
				DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
				System.out.println("????????????!"+deleteResponse.toString());

			}
		}

		System.out.println("?????????????????????");

		/*
		 * ???????????????????????????????????????
		 */

		// ?????????????????????????????????

		BulkProcessor.Listener listener = new BulkProcessor.Listener() {

			// ?????????BulkRequest???????????????????????????????????????????????????????????????BulkRequest???????????????????????????
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				int numberOfActions = request.numberOfActions();
				logger.debug("Executing bulk [{}] with {} requests", executionId, numberOfActions);
			}

			// ???????????????BulkRequest???????????????????????????????????????BulkResponse??????????????????
			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				if (response.hasFailures()) {
					logger.warn("Bulk [{}] executed with failures", executionId);
				} else {
					logger.debug("Bulk [{}] completed in {} milliseconds", executionId, response.getTook().getMillis());
				}
			}

			// ??????BulkRequest?????????????????????????????????????????????????????????
			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				logger.error("Failed to execute bulk", failure);
			}
		};

		BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer = (request2, bulkListener) -> client
				.bulkAsync(request, RequestOptions.DEFAULT, bulkListener);
		// ????????????????????????????????????
		BulkProcessor bulkProcessor = BulkProcessor.builder(bulkConsumer, listener).build();
		BulkProcessor.Builder builder = BulkProcessor.builder(bulkConsumer, listener);
		// ?????????????????????????????????????????????????????????????????????(?????????1000?????????-1?????????)
		builder.setBulkActions(500);
		// ?????????????????????????????????????????????????????????????????????(?????????5Mb?????????-1??????)
		builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));
		// ???????????????????????????????????????(?????????1?????????0???????????????????????????)
		builder.setConcurrentRequests(0);
		// ???????????????????????????????????????????????????????????????BulkRequest(??????????????????)
		builder.setFlushInterval(TimeValue.timeValueSeconds(10L));
		// ??????????????????????????????????????????????????????1??????????????????3??????
		builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3));

		IndexRequest one = new IndexRequest(index, type, "1").source(XContentType.JSON, "title",
				"In which order are my Elasticsearch queries executed?");
		IndexRequest two = new IndexRequest(index, type, "2").source(XContentType.JSON, "title",
				"Current status and upcoming changes in Elasticsearch");
		IndexRequest three = new IndexRequest(index, type, "3").source(XContentType.JSON, "title",
				"The Future of Federated Search in Elasticsearch");
		bulkProcessor.add(one);
		bulkProcessor.add(two);
		bulkProcessor.add(three);

		// ????????????????????????????????????????????????????????????true;?????????????????????????????????????????????????????????????????????????????????false
		boolean terminated = bulkProcessor.awaitClose(30L, TimeUnit.SECONDS);

		System.out.println("?????????????????????:" + terminated);

	}

}
