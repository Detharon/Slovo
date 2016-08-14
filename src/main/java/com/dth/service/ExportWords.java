package com.dth.service;

import com.dth.entity.WordOccurrence;
import java.util.List;

public interface ExportWords {
    public void export(List<WordOccurrence> words) throws ExportFailedException;
}
