package com.example.firealert.DTO;

import java.util.List;

public class ListHistory {
    private List<History> result;

    public ListHistory(List<History> result) {
        this.result = result;
    }

    public List<History> getResult() {
        return result;
    }

    public void setResult(List<History> result) {
        this.result = result;
    }
}
