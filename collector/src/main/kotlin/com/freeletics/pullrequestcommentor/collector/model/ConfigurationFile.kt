package com.freeletics.pullrequestcommentor.collector.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ConfigurationFile (
        /**
         * The path (incl. filename) where the resulting xml file should be stored
         */
        @JsonProperty("output") val outputFilePath : String,


        /**
         * The plugins that must be loaded
         */
        @JsonProperty("plugins") val pluginToLoad: List<PluginToLoad>

)