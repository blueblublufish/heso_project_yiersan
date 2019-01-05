package com.heso.data.entity;

import java.io.Serializable;

import org.dom4j.Node;

public class XmlContentObject implements Serializable{
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public Node getBody() {
		return body;
	}
	public void setBody(Node body) {
		this.body = body;
	}
	String transType;
	String msgId;
	String agentId;
	Node body;

}
