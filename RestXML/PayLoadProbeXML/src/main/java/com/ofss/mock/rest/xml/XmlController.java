package com.ofss.mock.rest.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofss.mock.rest.xml.dto.ApiEndpoints;
import com.ofss.mock.rest.xml.dto.ResponseMessageDTO;
import com.ofss.mock.rest.xml.dto.ServiceSpiDTO;

@Path("/")
public class XmlController {
	private XmlService xmlService;

	public XmlController() {
		this.xmlService = new XmlService();
	}

	@GET
	@Path("fetch/{key}")
	@Produces({ MediaType.TEXT_XML })
	public Response fetchXmlGET(@PathParam("key") String key) {

		String response = xmlService.getXmlByKey(key);
		if (response == null) {

			response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "<response>\r\n"
					+ "    <status>Failure</status>\r\n" + "    <data>\r\n"
					+ "        <response>No XML response found</response>\r\n" + "    </data>\r\n" + "</response>\r\n"
					+ "";
		}

		return Response.ok(response).build();
	}

	@POST
	@Path("fetch/{key}")
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.TEXT_XML })
	public Response fetchXmlPOST(@PathParam("key") String key) {

		String response = xmlService.getXmlByKey(key);
		if (response == null) {

			response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "<response>\r\n"
					+ "    <status>Failure</status>\r\n" + "    <data>\r\n"
					+ "        <response>No XML response found</response>\r\n" + "    </data>\r\n" + "</response>\r\n"
					+ "";
		}

		return Response.ok(response).build();
	}

	@POST
	@Path("add/{key}")
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response addXml(@PathParam("key") String Key, String XmlInput) {
		ResponseMessageDTO responseMessage = new ResponseMessageDTO();
		String response;
		response = xmlService.getXmlByKey(Key);
		if (response != null) {
			response = "Response for the given key is already present , try update or delete commands";
		} else {
			response = xmlService.modifyXml(Key, XmlInput, "added");
		}
		responseMessage.setMessage(response);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(responseMessage);
			return Response.ok(json).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\":\"Failed to serialize response\"}")
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
	}

	@POST
	@Path("update/{key}")
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateXml(@PathParam("key") String Key, String XmlInput) {
		ResponseMessageDTO responseMessage = new ResponseMessageDTO();
		String response;
		response = xmlService.getXmlByKey(Key);
		if (response == null) {
			response = "Response for the given key doesn't exists , try add command";
		} else {
			response = xmlService.modifyXml(Key, XmlInput, "updated");
		}
		responseMessage.setMessage(response);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(responseMessage);
			return Response.ok(json).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\":\"Failed to serialize response\"}")
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
	}

	@DELETE
	@Path("delete/{key}")
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteXml(@PathParam("key") String Key) {
		ResponseMessageDTO responseMessage = new ResponseMessageDTO();
		String response;
		response = xmlService.getXmlByKey(Key);
		if (response == null) {
			response = "Response for the given key doesn't exists , or has been deleted already";
		} else {
			response = xmlService.deleteXml(Key);
		}

		responseMessage.setMessage(response);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(responseMessage);
			return Response.ok(json).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("{\"message\":\"Failed to serialize response\"}")
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
	}

	@GET
	@Path("fetchAll")
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllSpi() {
		Set<String> spiNames = xmlService.getAllSpi();
		ResponseMessageDTO responseMessage = new ResponseMessageDTO();
		if (!spiNames.isEmpty()) {

			ServiceSpiDTO spiDTO = new ServiceSpiDTO();
			spiDTO.setServiceSpiCount(spiNames.size());
			for (String spiName : spiNames) {
				spiDTO.addToList(spiName);
			}
			ObjectMapper mapper = new ObjectMapper();
			JSONObject spiListJson = null;
			try {
				spiListJson = new JSONObject(mapper.writeValueAsString(spiDTO));
			} catch (Exception e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("").build();
			}
			return Response.ok(spiListJson.toString()).build();
			//
		} else {
			responseMessage.setMessage("No SPI responses found.");
			return Response.ok(responseMessage).build();
		}
	}

	@POST
	@Path("default")
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.TEXT_XML })
	public Response defaultXml() {

		String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + "<response>\r\n"
				+ "    <status>success</status>\r\n" + "    <data>\r\n" + "        <response>\r\n"
				+ "            <errorCode>0</errorCode>\r\n"
				+ "            <responseMessage>Default XML response</responseMessage>\r\n" + "        </response>\r\n"
				+ "    </data>\r\n" + "</response>\r\n" + "";

		return Response.ok(response.toString()).build();
	}

	@GET
	@Path("help")
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response helpManual() {
		List<ApiEndpoints> apiEndpoints = xmlService.getApiHelp();

		try {
			HashMap<String, List<ApiEndpoints>> responseMap = new HashMap<>();
			responseMap.put("endpoints", apiEndpoints);
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(responseMap);
			return Response.ok(jsonResponse).build();
		} catch (Exception e) {
			return Response.ok("Help needs help , try - http://100.76.143.10:7003/MockMiddlewareXML/application.wadl")
					.build();
		}

	}
}
