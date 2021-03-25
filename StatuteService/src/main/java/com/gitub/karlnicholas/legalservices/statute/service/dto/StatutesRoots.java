package com.gitub.karlnicholas.legalservices.statute.service.dto;

import java.util.List;

import com.github.karlnicholas.legalservices.statute.StatutesRoot;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatutesRoots {
	private List<StatutesRoot> statuteRoots;
}
