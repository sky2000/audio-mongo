package com.yan.audio.mongo.vo;

import java.io.Serializable;
import java.util.List;

public class ResponseVo implements Serializable{

	private static final long serialVersionUID = 1L;

	private Boolean success;
	
	/**
	 *  错误信息
	 */
	private String errorMsg;
	
	private Object result;
	
	/**
	 * 查询结果集
	 */
	private List results;

	/**
	 * 当前页
	 */
	private Integer pageNo;
	
	/**
	 * 每页条数
	 */
	private Integer pageSize;
	
	/**
	 * 总条数
	 */
	private Long totalCount;
	
	/**
	 * 总页数
	 */
	private Long totalPageCount;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public List getResults() {
		return results;
	}

	public void setResults(List results) {
		this.results = results;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public Long getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(Long totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
}
