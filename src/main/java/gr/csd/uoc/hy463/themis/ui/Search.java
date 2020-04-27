/*
 * themis - A fair search engine for scientific articles
 *
 * Currently over the Semantic Scholar Open Research Corpus
 * http://s2-public-api-prod.us-west-2.elasticbeanstalk.com/corpus/
 *
 * Collaborative work with the undergraduate/graduate students of
 * Information Retrieval Systems (hy463) course
 * Spring Semester 2020
 *
 * -- Writing code during COVID-19 pandemic times :-( --
 *
 * Aiming to participate in TREC 2020 Fair Ranking Track
 * https://fair-trec.github.io/
 *
 * Computer Science Department http://www.csd.uoc.gr
 * University of Crete
 * Greece
 *
 * LICENCE: TO BE ADDED
 *
 * Copyright 2020
 *
 */
package gr.csd.uoc.hy463.themis.ui;

import gr.csd.uoc.hy463.themis.indexer.Indexer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Some kind of simple ui to search the indexes. Some kind of GUI will be a
 * bonus!
 *
 * @author Panagiotis Papadakos <papadako at ics.forth.gr>
 */
public class Search {
    private static final Logger __LOGGER__ = LogManager.getLogger(CreateIndex.class);
    private Indexer _indexer = null;

    public void loadIndex() {
        if (isRunning()) {
            return;
        }
        try {
            if (_indexer != null) {
                _indexer.unload();
            }
            _indexer = new Indexer();
        } catch (IOException e) {
            __LOGGER__.error(e.getMessage());
            return;
        }
        _indexer.setTask(Indexer.TASK.LOAD_INDEX);
        Thread runnableIndexer = new Thread(_indexer);
        runnableIndexer.start();
    }

    public void unloadIndex() {
        try {
            if (_indexer != null) {
                _indexer.unload();
            }
        } catch (IOException e) {
            __LOGGER__.error(e.getMessage());
        }
    }

    public String getTask() {
        if (_indexer == null) {
            return null;
        }
        Indexer.TASK task = _indexer.getTask();
        if (task != null) {
            return task.toString();
        }
        return null;
    }

    public void stop() {
        if (_indexer != null) {
            _indexer.stop();
        }
    }

    public boolean isRunning() {
        return _indexer != null && _indexer.isRunning();
    }
}
