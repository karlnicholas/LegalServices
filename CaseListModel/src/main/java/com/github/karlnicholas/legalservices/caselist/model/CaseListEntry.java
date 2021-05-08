package com.github.karlnicholas.legalservices.caselist.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CaseListEntry {
	@EqualsAndHashCode.Include
	private String id;
	private String fileName;
	private String fileExtension;
	private String title;
	private LocalDate opinionDate;
	private LocalDate postedDate;
	private String court;
	private String searchUrl;
	private CASELISTSTATUS status;
	private int retryCount;
}