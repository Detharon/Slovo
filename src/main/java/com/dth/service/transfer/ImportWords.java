package com.dth.service.transfer;

import com.dth.entity.WordOccurrence;
import java.io.IOException;
import java.util.List;

public interface ImportWords {
    public List<WordOccurrence> importWords() throws TransferFailedException;
    public void close() throws IOException;
}
