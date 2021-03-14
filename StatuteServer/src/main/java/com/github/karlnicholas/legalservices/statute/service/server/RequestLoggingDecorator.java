package com.github.karlnicholas.legalservices.statute.service.server;

public class RequestLoggingDecorator{}
//public class RequestLoggingDecorator extends ServerHttpRequestDecorator {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingDecorator.class);
//
//	public RequestLoggingDecorator(ServerHttpRequest delegate) {
//		super(delegate);
//	}
//
//	@Override
//	public Flux<DataBuffer> getBody() {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		return super.getBody().doOnNext(dataBuffer -> {
//			try {
//				Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
//				String body = new String(baos.toByteArray(), StandardCharsets.UTF_8);
//				LOGGER.debug("Request: payload={}", body);
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					baos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//}