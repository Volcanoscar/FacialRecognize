package com.prize.facialrecognize.result;

import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */

public class MatchResult {

    /**
     * result_num : 1
     * results : [{"index_i":"0","index_j":"1","score":-3.814697265625E-6}]
     * log_id : 3443038033
     */

    private int result_num;
    private long log_id;
    private List<ResultsBean> results;

    public int getResult_num() {
        return result_num;
    }

    public void setResult_num(int result_num) {
        this.result_num = result_num;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * index_i : 0
         * index_j : 1
         * score : -3.814697265625E-6
         */

        private String index_i;
        private String index_j;
        private double score;

        public String getIndex_i() {
            return index_i;
        }

        public void setIndex_i(String index_i) {
            this.index_i = index_i;
        }

        public String getIndex_j() {
            return index_j;
        }

        public void setIndex_j(String index_j) {
            this.index_j = index_j;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        @Override
        public String toString() {
            return "ResultsBean{" +
                    "index_i='" + index_i + '\'' +
                    ", index_j='" + index_j + '\'' +
                    ", score=" + score +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "result_num=" + result_num +
                ", log_id=" + log_id +
                ", results=" + results +
                '}';
    }
}
