package gr.csd.uoc.hy463.themis.retrieval.models;

import gr.csd.uoc.hy463.themis.indexer.Exceptions.IndexNotLoadedException;
import gr.csd.uoc.hy463.themis.indexer.Indexer;
import gr.csd.uoc.hy463.themis.indexer.model.DocInfo;
import gr.csd.uoc.hy463.themis.retrieval.QueryTerm;
import gr.csd.uoc.hy463.themis.retrieval.model.Result;

import java.io.IOException;
import java.util.*;

/**
 * This is an abstract class that each retrieval model should extend.
 */
public abstract class Retrieval {
    public enum MODEL {
        OKAPI, VSM, EXISTENTIAL
    }

    protected int _totalDocuments;
    protected int _totalResults = 0;
    private double _documentPagerankWeight;
    protected Indexer _indexer;

    /**
     * Constructor.
     *
     * @param indexer
     * @throws IOException
     * @throws IndexNotLoadedException
     */
    protected Retrieval(Indexer indexer)
            throws IOException, IndexNotLoadedException {
        if (!indexer.isLoaded()) {
            throw new IndexNotLoadedException();
        }
        _indexer = indexer;
        _totalDocuments = indexer.getTotalDocuments();
        _documentPagerankWeight = indexer.getConfig().getDocumentPagerankWeight();
    }

    /**
     * Method that evaluates the query and returns a list of Results sorted by their scores (descending).
     * The list will contain a maximum endResult number of results.
     *
     * There are various policies to be faster when doing this if we do not want
     * to compute the scores of all queries.
     *
     * For example by sorting the terms of the query based on some indicator of
     * goodness and process the terms in this order (e.g., cutoff based on
     * document frequency, cutoff based on maximum estimated weight, and cutoff
     * based on the weight of a disk page in the posting list.
     *
     * @param query
     * @param endResult The returned list will have at most endResult results
     * @return
     * @throws IOException
     * @throws IndexNotLoadedException
     */
    public abstract List<Result> getRankedResults(List<QueryTerm> query, int endResult)
            throws IOException, IndexNotLoadedException;

    /**
     * Sorts the specified results. Uses the pagerank scores of the documents and the scores from the
     * retrieval model. The returned list will contain a maximum of endResult number of results.
     *
     * @param results
     * @param endResult
     * @return
     */
    protected List<Result> sort(List<Result> results, int endResult)
            throws IndexNotLoadedException {
        boolean hasPagerank = Double.compare(_documentPagerankWeight, 0.0) != 0;
        if (hasPagerank) {
            double maxPagerankScore = 0;
            double modelWeight = 1 - _documentPagerankWeight;
            double[] documentsPagerank = _indexer.getDocumentsPagerank();

            //normalize pagerank scores
            for (int i = 0; i < results.size(); i++) {
                DocInfo docInfo = results.get(i).getDocInfo();
                double pagerankScore = documentsPagerank[docInfo.getDocID()];
                if (pagerankScore > maxPagerankScore) {
                    maxPagerankScore = pagerankScore;
                }
            }
            if (Double.compare(maxPagerankScore, 0.0) == 0) {
                maxPagerankScore = 1;
            }

            //calculate the final score
            for (int i = 0; i < results.size(); i++) {
                DocInfo docInfo = results.get(i).getDocInfo();
                double modelScore = results.get(i).getScore();
                double pagerankScore = documentsPagerank[docInfo.getDocID()] / maxPagerankScore;
                double combinedScore = modelScore * modelWeight + pagerankScore * _documentPagerankWeight;
                results.get(i).setScore(combinedScore);
            }
        }

        //sort results (descending)
        Collections.sort(results);

        //return at most endResults results
        int finalSize = Math.min(endResult, results.size());
        if (finalSize == results.size()) {
            return results;
        }
        else {
            List<Result> topResults = new ArrayList<>();
            for (int i = 0; i < finalSize; i++) {
                topResults.add(results.get(i));
            }
            return topResults;
        }
    }

    /**
     * Sets the weight of the Pagerank scores of the documents
     *
     * @param weight
     */
    public void setDocumentPagerankWeight(double weight) {
        _documentPagerankWeight = weight;
    }

    /**
     * Gets the weight of the pagerank scores of the documents
     *
     * @return
     */
    public double getDocumentPagerankWeight() {
        return _documentPagerankWeight;
    }

    /**
     * Returns the total number of results of the last query
     *
     * @return
     */
    public int getTotalResults() {
        return _totalResults;
    }

    /**
     * Merges (adds) the weights of the equal terms and returns a new list
     *
     * @param query
     * @return
     */
    protected List<QueryTerm> mergeTerms(List<QueryTerm> query) {
        List<QueryTerm> mergedQuery = new ArrayList<>();
        for (int i = 0; i < query.size(); i++) {
            QueryTerm currentTerm = query.get(i);
            boolean found = false;
            for (int j = 0; j < i; j++) {
                if (currentTerm.get_term().equals(query.get(j).get_term())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                QueryTerm mergedTerm = new QueryTerm(currentTerm.get_term(), currentTerm.get_weight());
                for (int j = i + 1; j < query.size(); j++) {
                    if (currentTerm.get_term().equals(query.get(j).get_term())) {
                        mergedTerm.set_weight(mergedTerm.get_weight() + query.get(j).get_weight());
                    }
                }
                mergedQuery.add(mergedTerm);
            }
        }
        return mergedQuery;
    }
}
