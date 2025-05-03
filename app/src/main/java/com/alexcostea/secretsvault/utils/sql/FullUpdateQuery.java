package com.alexcostea.secretsvault.utils.sql;

import java.util.List;

public record FullUpdateQuery(String deleteQuery, String insertQuery,
                              List<String> insertParameters) {
}
