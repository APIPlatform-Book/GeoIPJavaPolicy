function PolicyConfigurationModel(ko, $, oj, config, additionalParams) {
	
    self = this;
    self.config = config;
    self.l10n = ko.observable(additionalParams.l10nbundle);
    self.conditions = ko.observableArray();
    self.country = ko.observable();
    self.GeoFilename = ko.observable();
    self.NetworkFilename = ko.observable();
    
    self.initialize = function() {
    	
        if (self.config) {
        	
            self.country(self.config.country);
            self.GeoFilename(self.config.GeoFilename);
            self.NetworkFilename(self.config.NetworkFilename);            
            
        }
        
    };

    self.initialize();
    
}