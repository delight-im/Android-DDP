package meteor.android.todo;

/**
 * Created by Sadmansamee on 2/3/16.
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "incompleteCount"
})
public class Lists {

    @JsonProperty("name")
    private String name;
    @JsonProperty("incompleteCount")
    private Integer incompleteCount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The incompleteCount
     */
    @JsonProperty("incompleteCount")
    public Integer getIncompleteCount() {
        return incompleteCount;
    }

    /**
     * @param incompleteCount The incompleteCount
     */
    @JsonProperty("incompleteCount")
    public void setIncompleteCount(Integer incompleteCount) {
        this.incompleteCount = incompleteCount;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}


