package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.util.List;

public interface ExportWords {
    public void exportWords(List<WordOccurrence> words) throws TransferFailedException;
}
