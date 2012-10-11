var DecisionTree = {};

DecisionTree.TextToSpeechPrompt = function() {
    this["@type"] = "org.motechproject.decisiontree.core.model.TextToSpeechPrompt";
    this.message = "";
}

DecisionTree.AudioPrompt = function() {
    this["@type"] = "org.motechproject.decisiontree.core.model.AudioPrompt";
    this.message = "";
}

DecisionTree.PolymorphicPrompt = function(promptType) {
    this["@type"] = "PolymorphicPrompt";
    this.promptType = promptType;
    this.availableTypes = {"TextToSpeechPrompt" : DecisionTree.TextToSpeechPrompt, "AudioPrompt": DecisionTree.AudioPrompt};
    this.selectedPrompt = null;
}