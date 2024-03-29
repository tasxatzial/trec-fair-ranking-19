package gr.csd.uoc.hy463.themis.lexicalAnalysis.collections.SemanticScholar;

import gr.csd.uoc.hy463.themis.utils.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for reading textual entries from the JSON description of the documents
 */
public class S2JsonEntryReader {
    private static final Logger __LOGGER__ = LogManager.getLogger(S2JsonEntryReader.class);

    /**
     * Reads the doc ID from a JSON string
     *
     * @param jsonToRead
     * @return
     */
    public static S2TextualEntry readDocIDEntry(String jsonToRead) {
        S2TextualEntry entry = new S2TextualEntry();
        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(jsonToRead);
        }
        catch (ParseException e) {
            __LOGGER__.error(e);
            return entry;
        }

        // This should be a JSON object.
        JSONObject jsonObject = (JSONObject) obj;

        String ID = (String) jsonObject.get("id");
        entry.setID(ID);

        return entry;
    }

    /**
     * Reads all textual information from a JSON string (except IN and OUT citations)
     *
     * @param jsonToRead
     * @return
     */
    public static S2TextualEntry readTextualEntry(String jsonToRead) {
        S2TextualEntry entry = new S2TextualEntry();
        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(jsonToRead);
        }
        catch (ParseException e) {
            __LOGGER__.error(e);
            return entry;
        }

        // This should be a JSON object.
        JSONObject jsonObject = (JSONObject) obj;

        // Get the ID
        String ID = (String) jsonObject.get("id");
        entry.setID(ID);

        // Get the title
        String titleCheck = (String) jsonObject.get("title");
        String title = titleCheck != null ? titleCheck : "";
        entry.setTitle(title);

        // Get abstract
        String paperAbstractCheck = (String) jsonObject.get("paperAbstract");
        String paperAbstract = paperAbstractCheck != null ? paperAbstractCheck : "";
        entry.setPaperAbstract(paperAbstract);

        // Read entities. A JSONArray
        JSONArray entitiesArray = (JSONArray) jsonObject.get("entities");
        List<String> entities = new ArrayList<>();
        if(entitiesArray != null) {
            entitiesArray.forEach(entity -> {
                entities.add(entity.toString());
            });
        }
        entry.setEntities(entities);

        // Read fieldsOfStudy. A JSONArray
        JSONArray fieldsArray = (JSONArray) jsonObject.get("fieldsOfStudy");
        List<String> fields = new ArrayList<>();
        if(fieldsArray != null) {
            fieldsArray.forEach(field -> {
                fields.add(field.toString());
            });
        }
        entry.setFieldsOfStudy(fields);

        // Read authors. A JSONArray
        JSONArray authorsList = (JSONArray) jsonObject.get("authors");
        List<Pair<String, List<String>>> authors = new ArrayList<>();
        if (authorsList != null) {
            for (int i = 0; i < authorsList.size(); i++) {
                JSONObject authorInfo = (JSONObject) authorsList.get(i);
                String authorName = (String) authorInfo.get("name");
                // Now get all the ids
                JSONArray IDsList = (JSONArray) authorInfo.get("ids");
                List<String> IDs = new ArrayList<>();
                if(IDsList != null) {
                    for (int j = 0; j < IDsList.size(); j++) {
                        ID = (String) IDsList.get(j);
                        IDs.add(ID);
                    }
                }
                Pair author = new Pair(authorName, IDs);
                authors.add(author);
            }
        }
        entry.setAuthors(authors);

        // Get journal
        String journalCheck = (String) jsonObject.get("journalName");
        String journal = journalCheck != null ? journalCheck : "";
        entry.setJournalName(journal);

        // Read sources. A JSONArray
        JSONArray sourcesArray = (JSONArray) jsonObject.get("sources");
        List<String> sources = new ArrayList<>();
        if(sourcesArray != null) {
            sourcesArray.forEach(source -> {
                sources.add(source.toString());
            });
        }
        entry.setSources(sources);

        // Get year
        Long yearLong = (Long) jsonObject.get("year");
        int year = yearLong != null ? yearLong.intValue() : 0;
        entry.setYear(year);

        // Get venue
        String venueCheck = (String) jsonObject.get("venue");
        String venue = venueCheck != null ? venueCheck : "";
        entry.setVenue(venue);

        return entry;
    }

    /**
     * Reads the IN and OUT citations from a JSON string
     *
     * @param jsonToRead
     * @return
     */
    public static S2TextualEntry readCitationsEntry(String jsonToRead) {
        S2TextualEntry entry = new S2TextualEntry();
        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(jsonToRead);
        } catch (ParseException e) {
            __LOGGER__.error(e);
            return entry;
        }

        // This should be a JSON object.
        JSONObject jsonObject = (JSONObject) obj;

        // Get the ID
        String ID = (String) jsonObject.get("id");
        entry.setID(ID);

        // Read out citations. A JSONArray
        JSONArray outCitationsArray = (JSONArray) jsonObject.get("outCitations");
        List<String> outCitations = new ArrayList<>();
        if(outCitationsArray != null) {
            outCitationsArray.forEach(citation -> {
                outCitations.add(citation.toString());
            });
        }
        entry.setOutCitations(outCitations);

        JSONArray inCitationsArray = (JSONArray) jsonObject.get("inCitations");
        List<String> inCitations = new ArrayList<>();
        if(inCitationsArray != null) {
            inCitationsArray.forEach(citation -> {
                inCitations.add(citation.toString());
            });
        }
        entry.setInCitations(inCitations);

        return entry;
    }

    public static void main(String[] args) throws IOException {
        String json = "{\n"
                + "	\"entities\": [],\n"
                + "	\"journalVolume\": \"\",\n"
                + "	\"journalPages\": \"\",\n"
                + "	\"pmid\": \"\",\n"
                + "	\"fieldsOfStudy\": [\"Physics\"],\n"
                + "	\"year\": 2015,\n"
                + "	\"outCitations\": [\"2497ed63572e8d5e5fe7945f0b23e0d090acd51c\", \"03b317054274da28acfb2c8e082f38d7dcfdce04\", \"070c58ff3d4f5ca3383c20a23af3594ae6e564ab\", \"f9a1951720cafa3706b341c0d14ddd57d9c83043\", \"26052227014c270c3f6013d98fdb8db1b80f8607\", \"8de63b8021633e45585874468ff5fe4bfe3ee476\", \"91d9b8d56ce67a90abfe0c9fc7483b8220ad3c66\", \"5baead167bceac9bdcbd7ac808620bb8987da323\", \"778f2e33cb7b0dfc0d3925df852fa4e576e75890\", \"88a11402d59f026ae5cd93f044e0c038f4373d51\", \"c2038b5d11a4dd9017d7a410b93f088f4dc8d1e4\", \"fa1aff91383e227fc115fc0621fd7452ebca46ab\"],\n"
                + "	\"s2Url\": \"https://semanticscholar.org/paper/1b2f4e5be76a0a746b72110b447b42fffa046b5c\",\n"
                + "	\"s2PdfUrl\": \"\",\n"
                + "	\"id\": \"1b2f4e5be76a0a746b72110b447b42fffa046b5c\",\n"
                + "	\"authors\": [{\n"
                + "		\"name\": \"Xiang Fa Liu\",\n"
                + "		\"ids\": [\"153201706\"]\n"
                + "	}, {\n"
                + "		\"name\": \"Guodong Xia\",\n"
                + "		\"ids\": [\"46932503\"]\n"
                + "	}, {\n"
                + "		\"name\": \"Guo-zhen Yang\",\n"
                + "		\"ids\": [\"50147063\"]\n"
                + "	}],\n"
                + "	\"journalName\": \"\",\n"
                + "	\"paperAbstract\": \"Abstract Experimental investigations on the characteristics of air–water two-phase flow in the vertical helical rectangular channel are performed using the high speed flow visualization. The flow pattern map and the transition in the helical rectangular channel are presented. The flow pattern evolution in different positions of the helical rectangular channel is illustrated. The discussion on the coalescence of the bubble and slug is presented. The slug velocity, slug length distribution, liquid slug frequency, falling liquid film velocity and falling film thickness along the slug are investigated. The dimensionless liquid film thickness of the annular flow on the outer side of the channel is measured using the digital image processing algorithm.\",\n"
                + "	\"inCitations\": [],\n"
                + "	\"pdfUrls\": [\"http://www.mechwork.ir/uploads/papers/4-Experimental%20study%20on%20the%20characteristics%20of%20air-water%20two-phase%20flow%20in%20vertical.pdf\"],\n"
                + "	\"title\": \"Experimental study on the characteristics of air–water two-phase flow in vertical helical rectangular channel\",\n"
                + "	\"doi\": \"10.1016/j.ijmultiphaseflow.2015.03.012\",\n"
                + "	\"sources\": [],\n"
                + "	\"doiUrl\": \"https://doi.org/10.1016/j.ijmultiphaseflow.2015.03.012\",\n"
                + "	\"venue\": \"\"\n"
                + "}";

        System.out.println(S2JsonEntryReader.readTextualEntry(json));
        System.out.println(S2JsonEntryReader.readCitationsEntry(json));
    }
}
