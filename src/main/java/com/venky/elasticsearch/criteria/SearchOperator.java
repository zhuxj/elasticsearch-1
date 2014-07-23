package com.venky.elasticsearch.criteria;

public enum SearchOperator {
	FULL_TEXT_OPERATOR {
		@Override
		public String toString() {
			return "~";
		}
	},
	EXACT_MATCH_OPERATOR {
		@Override
		public String toString() {
			return ":";
		}
	}
}
