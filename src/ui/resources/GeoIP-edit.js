function PolicyConfigurationModel(ko, $, oj, config, additionalParams) {
	
    self = this;
    self.config = config;
    self.country = ko.observable("");
    self.GeoFilename = ko.observable("");
    self.NetworkFilename = ko.observable("");
    self.l10n = ko.observable(additionalParams.l10nbundle);
    self.disableApplyPolicyButton = additionalParams.disableApplyPolicyButton;

    self.initialize = function() {
    	
        if (self.config) {
        	
            self.country(config.country);
            self.GeoFilename(config.GeoFilename);
            self.NetworkFilename(config.NetworkFilename);
        }
        
        self.disableApplyButton(false);
        
    };

    self.getPolicyConfiguration = function() {
    	
        var config = {"country" : "", "GeoFilename" : "", "NetworkFilename" : ""};
        config.country  = self.country();;
        config.GeoFilename  = self.GeoFilename();;
        config.NetworkFilename  = self.NetworkFilename();;
        
        return config;
        
    };

    self.disableApplyButton = function(flag) {
    	
        self.disableApplyPolicyButton(flag);
        
    };

    self.initialize();
    
}