/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License./
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var arc = function(dataTable, config) {
      this.metadata = dataTable[0].metadata;
      var marks =[];
      this.spec = {};

      config = checkConfig(config, this.metadata);
      this.config = config;
      dataTable[0].name= config.title;

      var summarize = {};
      summarize[this.metadata.names[config.x]] = "sum";

      dataTable.push({
      "name": "summary",
      "source": config.title,
      "transform": [
        {
          "type": "aggregate",
          "summarize": summarize
        }
      ]
      });

      dataTable.push( {
      "name": "layout",
      "source": "table",
      "transform": [
        {"type": "cross", "with": "summary"},
        {"type": "pie","field": "a." + this.metadata.names[config.x]},
        {
          "type": "formula",
          "field": "percentage",
          "expr": "datum.a."+this.metadata.names[config.x]
          +" / datum.b.sum_"+this.metadata.names[config.x]+" * 100"
        }
      ]
    });
      
      var scales =  []; 

      if (config.colorDomain == -1) {
         config.colorDomain = {"data":  config.title, "field": this.metadata.names[config.color]};
      }


       var colorScale = {
                    "name": "color", 
                    "type": "ordinal", 
                    "domain": config.colorDomain,
                    "range": config.colorScale
                      };
      scales.push(colorScale);


      if (config.percentage && 
        (config.mode == "pie" || config.mode == "donut")) {
        marks.push(getPieText(config, this.metadata));
      } else if (config.percentage) {
        //Push complimentary value to fill the arc
        dataTable.push(    
            {
          "name": "arc",
          "values": [{"type": "YES"}]
        });
        marks.push(getPieMidText(config, this.metadata));;
      }

      marks.push(getPieMark(config, this.metadata));
      
      var legendTitle = "Legend";

      if (config.title != "table") {
          legendTitle = config.title;
      }

      if (this.config.legend) {
         this.spec.legends = getLegend(this.config);
      }
      
      this.spec.width = config.width;
      this.spec.height = config.height;
      this.spec.data = dataTable;
      this.spec.scales = scales;
      this.spec.padding = config.padding;
      this.spec.marks = marks;
};

arc.prototype.draw = function(div, callbacks) {

    var viewUpdateFunction = (function(chart) {
      if(this.config.tooltip.enabled){
        this.config.tooltip.type = "arc";
        createTooltip(div);
        this.view = chart({el:div}).renderer(this.config.renderer).update();
        bindTooltip(div,this.view,this.config,this.metadata);
      } else {
         this.view = chart({el:div}).renderer(this.config.renderer).update();
      }

       if (callbacks != null) {
          for (var i = 0; i<callbacks.length; i++) {
            this.view.on(callbacks[i].type, callbacks[i].callback);
          }
       }

    }).bind(this);

    if(this.config.maxLength != -1){
        var dataset = this.spec.data[0].values;
        var maxValue = this.config.maxLength;
        if(dataset.length >= this.config.maxLength){
            var allowedDataSet = [];
            var startingPoint = dataset.length - maxValue;
            for(var i = startingPoint; i < dataset.length;i++){
                allowedDataSet.push(dataset[i]);
            }
            this.spec.data[0].values = allowedDataSet;
        }
    }

    vg.parse.spec(this.spec, viewUpdateFunction);
};

arc.prototype.insert = function(data) {

    var color = this.metadata.names[this.config.color];
    var x = this.metadata.names[this.config.x];
    var view =this.view;



    var updated = false;

    for (i = 0; i < data.length; i++) {
          this.view.data(this.config.title)
           .update(
            function(d) { 
              return d[color] == data[i][color]; 
            }, 
            x, 
            function(d) { 
              updated = true;
              return data[i][x]; 
            });  
        }

        if (updated == false) {
          view.data(this.config.title).insert(data);
        }

     this.view.update({duration: 500});
};

arc.prototype.getSpec = function() {
  return this.spec;
};


function getPieMark(config, metadata){
        var innerRadius;
        if (config.mode == "donut") { 
          var innerRadius = config.width / 5 * ( 1 + config.innerRadius);
        } else if (config.mode == "pie") {
          var innerRadius = 0;
        } else {
          config.innerRadius += 0.5;
          var innerRadius = config.width / 5 * ( 1 + config.innerRadius);
        }

        var title = config.title;
        var fieldAlias = ""

        if (config.type != null) {
          title = "layout";
          fieldAlias = "a.";
        }

        var mark =  {
                      "type": "arc",
                      "from":  {"data": title},
                      "properties": {
                        "update": {
                          "x": {"field": {"group": "width"}, "mult": 0.5},
                          "y": {"field": {"group": "height"}, "mult": 0.5},
                          "startAngle": {"field": "layout_start"},
                          "endAngle": {"field": "layout_end"},
                          "innerRadius": {"value": innerRadius},
                          "outerRadius":  {"value": config.width * 0.4},
                          "fill": {"scale": "color", "field": fieldAlias + metadata.names[config.color]},
                          "fillOpacity": {"value": 1}
                        },

                        "hover": {
                          "fillOpacity": {"value": 0.8},
                          "cursor": {"value": config.hoverCursor}
                        }
                      }
                    };

        return mark;
};
function getPieMidText(config, metadata){
        var mark =      {
                          "type": "text",
                          "from": {"data": "layout"},
                          "properties": {
                            "update": {
                              "x": {"field": {"group": "width"}, "mult": 0.5},
                              "y": {"field": {"group": "height"}, "mult": 0.5},
                              "radius": { "value": 0},
                              "theta": {"field": "layout_mid"},
                              "fill": [
                                      {
                                        "test": "indata('arc', datum.a."+metadata.names[config.color]+", 'type')",
                                        "scale": "color", "field": metadata.names[config.color]
                                      },
                                      {}
                                    ],
                              "align": {"value": "center"},
                              "baseline": {"value": "middle"},
                              "fontSize":{"value": config.width/9},
                              "text": {"template": "{{datum.percentage | number:'.2f'}}%"}

                             }
                          }
                        };
        return mark;
};


function getPieText(config, metadata){
        var mark =      {
                          "type": "text",
                          "from": {"data": "layout"},
                          "properties": {
                            "update": {
                              "x": {"field": {"group": "width"}, "mult": 0.5},
                              "y": {"field": {"group": "height"}, "mult": 0.5},
                              "radius": { "value": config.width * 0.5},
                              "theta": {"field": "layout_mid"},
                              "fill": {"value": "#000"},
                              "align": {"value": "center"},
                              "baseline": {"value": "middle"},
                              "text": {"template": "{{datum.percentage | number:'.2f'}}%"}

                            }
                          }
                        };

        return mark;
};
;
var area = function(dataTable, config) {
    this.metadata = dataTable[0].metadata;
    var marks =[];
    var signals = [];
    this.spec = {};

    config = checkConfig(config, this.metadata);
    this.config = config;
    dataTable[0].name= config.title;

    var scales =  getXYScales(config, this.metadata);
    //Make Y scale zero false as area should filled to minimum value
    delete scales[1].zero;

    if (config.color != -1) {

        if (config.colorDomain == -1) {
            config.colorDomain = {"data":  config.title, "field": this.metadata.names[config.color]};
        }

        var colorScale = {
            "name": "color",
            "type": "ordinal",
            "domain": config.colorDomain,
            "range": config.colorScale
        };
        scales.push(colorScale);

        var legendTitle = "Legend";

        if (config.title != "table") {
            legendTitle = config.title;
        }

      if (this.config.legend) {
         this.spec.legends = getLegend(this.config);
      }
    }


    var axes =  getXYAxes(config, "x", "x", "y", "y");

      marks.push(getAreaMark(config, this.metadata));
      config.fillOpacity  = 0;
      config.markSize = 1000;
      marks.push(getSymbolMark(config, this.metadata));

      if (config.range) {
         signals = getRangeSignals(config, signals);
         marks = getRangeMark(config, marks);
      }
      
      this.spec.width = config.width;
      this.spec.height = config.height;
      this.spec.axes = axes;
      this.spec.data = dataTable;
      this.spec.scales = scales;
      this.spec.padding = config.padding;
      this.spec.marks = marks;
      this.spec.signals = signals;
};

area.prototype.draw = function(div, callbacks) {

    if(this.config.maxLength != -1){
        var dataset = this.spec.data[0].values;
        var maxValue = this.config.maxLength;
        if(dataset.length >= this.config.maxLength){
            var allowedDataSet = [];
            var startingPoint = dataset.length - maxValue;
            for(var i = startingPoint; i < dataset.length;i++){
                allowedDataSet.push(dataset[i]);
            }
            this.spec.data[0].values = allowedDataSet;
        }
    }

     drawChart(div, this, callbacks);

};

area.prototype.insert = function(data) {
    //Removing events when max value is enabled
    if (this.config.maxLength != -1 && this.config.maxLength <  (this.view.data(this.config.title).values().length + data.length)) {
        var removeFunction = (function(d) { 
          return d[this.metadata.names[this.config.x]] == oldData; 
        }).bind(this);

        for (i = 0; i < data.length; i++) {
          var oldData = this.view.data(this.config.title).values()[i][this.metadata.names[this.config.x]];
          this.view.data(this.config.title).remove(removeFunction);  
        }
    } 

     this.view.data(this.config.title).insert(data);
     this.view.update();
};

area.prototype.getSpec = function() {
  return this.spec;
};


function getAreaMark(config, metadata){

    var mark;
    if (config.color != -1) {
        mark =  {
            "type": "group",
            "from": {
                "data":  config.title,
                "transform": [{"type": "facet", "groupby": [metadata.names[config.color]]}]
            },
            "marks": [
                {
                    "type": "area",
                    "properties": {
                        "update": {
                            "x": {"scale": "x", "field": metadata.names[config.x]},
                            "y": {"scale": "y", "field": metadata.names[config.y]},
                            "y2": {"scale": "y", "value": 0},
                            "fill": {"scale": "color", "field": metadata.names[config.color]},
                            "strokeWidth": {"value": 2},
                            "strokeOpacity": {"value": 1}
                        },
                        "hover": {
                            "strokeOpacity": {"value": 0.5}
                        }
                    }
                }
            ]
        };
    }else{
        mark = {
            "type": "area",
            "from": {"data": config.title},
            "properties": {
                "update": {

                    "x": {"scale": "x", "field": metadata.names[config.x]},
                    "y": {"scale": "y", "field": metadata.names[config.y]},
                    "y2": {"scale": "y", "value": 0},
                    "fill": { "value": config.markColor},
                    "strokeWidth": {"value": 2},
                    "fillOpacity": {"value": 1}
                },
                "hover": {
                    "fillOpacity": {"value": 0.5}
                }
            }
        };
    }


    return mark;
}

;
var bar = function(dataTable, config) {
      this.metadata = dataTable[0].metadata;
      var marks =[];
      var scales =[];
      this.spec = {};
      var yColumn;
      var yDomain;

      var xRange;
      var yRange;
      var xAxesType;
      var yAxesType;
      var signals = [];

      config = checkConfig(config, this.metadata);
      this.config = config;
      dataTable[0].name= config.title;

      if (config.orientation == "left") {
        xRange = "height";
        yRange = "width";
        xAxesType = "y";
        yAxesType = "x";
      } else {
        xRange = "width";
        yRange = "height";
        xAxesType = "x";
        yAxesType = "y";
      }
      
      if (config.color != -1) {
        var legendTitle = "Legend";
      if (config.title != "table") {
          legendTitle = config.title;
      }
        if (config.colorDomain == -1) {
              config.colorDomain = {"data":  config.title, "field": this.metadata.names[config.color]};
          }

          var colorScale = {
            "name": "color", 
            "type": "ordinal", 
            "domain": config.colorDomain,
            "range": config.colorScale
          };

          scales.push(colorScale);



          if (config.mode == "stack") {
            var aggregateData = {
              "name": "stack",
              "source": config.title,
              "transform": [
                {
                  "type": "aggregate",
                  "groupby": [this.metadata.names[config.x]],
                  "summarize": [{"field": this.metadata.names[config.y], "ops": ["sum"]}]
                }
              ]
            };

            dataTable.push(aggregateData);
            yColumn = "sum_"+ this.metadata.names[config.y];
            yDomain = "stack";

        } else {
            yColumn = this.metadata.names[config.y];
            yDomain = config.title;
        }
        
        if (this.config.legend) {
          this.spec.legends = getLegend(this.config);
        }
      } else {
        yColumn = this.metadata.names[config.y];
        yDomain = config.title;
      }

      var xScale = {
              "name": "x",
              "type": "ordinal",
              "range": xRange,
              "domain": config.xScaleDomain
              };

    if (config.mode == "group") {
        xScale.padding = 0.2;
      }

      if (config.yScaleDomain.constructor !== Array) {
          config.yScaleDomain = {"data": yDomain, "field": yColumn};
      }

      var yScale = {
          "name": "y",
          "type": this.metadata.types[config.y],
          "range": yRange,
          "domain": config.yScaleDomain
          };


      
      scales.push(xScale);
      scales.push(yScale);

      var axes =  getXYAxes(config, xAxesType, "x", yAxesType, "y");

      if (config.color != -1 && config.mode == "stack") {
        marks.push(getStackBarMark(config, this.metadata));
      } else if (config.color != -1 && config.mode == "group") {
        marks.push(getGroupBarMark(config, this.metadata));
      } else {
        marks.push(getBarMark(config, this.metadata));
      }

      if (config.range) {
         signals = getRangeSignals(config, signals);
         marks = getRangeMark(config, marks);
      }

      if (config.orientation == "left" && config.text != null) {

        var xVal = {"value": 5};

        if (config.textAlign == "right") {
          var xVal = {"scale": "y","field": this.metadata.names[config.y], "offset": 2};
        }

        marks.push({
                "type": "text",
                "from": {"data": "table"},
                "properties": {
                  "update": {
                      "x": xVal,
                    "dy": {
                      "scale": "x",
                      "band": true,
                      "mult": 0.5
                    },
                    "y": {"scale": "x","field": this.metadata.names[config.x]},
                    "align":{"value": "left"},
                    "text": {"field": this.metadata.names[config.x]},
                    "fill": {"value": config.textColor}
                  }
                }
              });
      }

      if (config.highlight == "single" || config.highlight == "multi") {

        var multiTest;

        if (config.highlight == "multi") {
          multiTest = "!multi";
        } else {
          multiTest = "multi";
        }


        dataTable.push(   
          {
            "name": "selectedPoints",
            "modify": [
              {"type": "clear", "test": multiTest},
              {"type": "toggle", "signal": "clickedPoint", "field": "id"}
            ]
          });

          signals.push(    {
              "name": "clickedPoint",
              "init": 0,
              "verbose": true,
              "streams": [{"type": "click", "expr": "datum._id"}]
            },
            {
              "name": "multi",
              "init": false,
              "verbose": true,
              "streams": [{"type": "click", "expr": "datum._id"}]
            });

          marks[0].properties.update.fill = [
            {
              "test": "indata('selectedPoints', datum._id, 'id')",
              "value": config.selectionColor
            },marks[0].properties.update.fill
          ];
      }

      this.spec.width = config.width;
      this.spec.height = config.height;
      this.spec.axes = axes;
      this.spec.data = dataTable;
      this.spec.scales = scales;
      this.spec.padding = config.padding;
      this.spec.marks = marks;
      this.spec.signals = signals;

      var specc = JSON.stringify(this.spec);
};

bar.prototype.draw = function(div, callbacks) {
  if(this.config.maxLength != -1){
        var dataset = this.spec.data[0].values;
        var maxValue = this.config.maxLength;
        if(dataset.length >= this.config.maxLength){
            var allowedDataSet = [];
            var startingPoint = dataset.length - maxValue;
            for(var i = startingPoint; i < dataset.length;i++){
                allowedDataSet.push(dataset[i]);
            }
            this.spec.data[0].values = allowedDataSet;
        }
    }
    this.config.tooltip.type = "rect";
    drawChart(div, this, callbacks);
};

bar.prototype.insert = function(data) {

    var xAxis = this.metadata.names[this.config.x];
    var yAxis = this.metadata.names[this.config.y];
    var size = this.metadata.names[this.config.size];
    var color = this.metadata.names[this.config.color];

    if (this.config.maxLength != -1 && this.config.maxLength <  (this.view.data(this.config.title).values().length + data.length)) {

        var allDataSet = this.view.data(this.config.title).values().concat(data);
        var allowedRemovableDataSet = [];
        for (i = 0; i < allDataSet.length - this.config.maxLength; i++) {
            allowedRemovableDataSet.push(this.view.data(this.config.title).values()[i][xAxis]);
        }

        for (i = 0; i < data.length; i++) {
            var isValueMatched = false;
            this.view.data(this.config.title).update(function(d) {
                    if (color == null) {
                      return d[xAxis] == data[i][xAxis]; 
                    } else {
                      return d[xAxis] == data[i][xAxis] &&  d[color] == data[i][color];
                    }
                  },
                yAxis,
                function(d) {
                    isValueMatched = true;
                    return data[i][yAxis];
                });

            if(isValueMatched){
                var isIndexRemoved = false;

                var index = allowedRemovableDataSet.indexOf(data[i][xAxis]);
                if (index > -1) {
                    // updated value matched in allowed removable values
                    isIndexRemoved = true;
                    allowedRemovableDataSet.splice(index, 1);
                }

                if(!isIndexRemoved){
                    // updated value NOT matched in allowed removable values
                    allowedRemovableDataSet.splice((allowedRemovableDataSet.length - 1), 1);
                }

            } else {
                //insert the new data
                this.view.data(this.config.title).insert([data[i]]);
                this.view.update();
            }
        }

        var oldData;
        var removeFunction = function(d) {
            return d[xAxis] == oldData;
        };

        for (i = 0; i < allowedRemovableDataSet.length; i++) {
            oldData = allowedRemovableDataSet[i];
            this.view.data(this.config.title).remove(removeFunction);
        }
    } else{
        for (i = 0; i < data.length; i++) {
            var isValueMatched = false;
            this.view.data(this.config.title).update(function(d) {
                  if (color == null) {
                    return  d[xAxis] == data[i][xAxis]; 
                  } else {
                    return  d[xAxis] == data[i][xAxis] &&  d[color] == data[i][color];
                  }
                },
                yAxis,
                function(d) {
                    isValueMatched = true;
                    return data[i][yAxis];
                });

            if(!isValueMatched){
                this.view.data(this.config.title).insert([data[i]]);
            }
        }
    }

    //Group does not support duration update animation
    if (this.config.mode == "group") {
      this.view.update();
    } else {
      this.view.update({duration: 200});
    }
};

bar.prototype.getSpec = function() {
  return this.spec;
};

bar.prototype.setSpec = function(spec) {
  this.spec = spec;
}


function getBarMark(config, metadata){
  var markContent;
  if (config.orientation == "left") {
    markContent = {
                    "y": {"scale": "x", "field": metadata.names[config.x]},
                    "height": {"scale": "x", "band": true, "offset": calculateBarGap(config)},
                    "x": {"scale": "y", "field": metadata.names[config.y]},
                    "x2": {"scale": "y", "value": 0},
                    "fill": {"value": config.markColor},
                    "fillOpacity": {"value": 1}
                  };
  } else {
    markContent = {
                    "x": {"scale": "x", "field": metadata.names[config.x]},
                    "width": {"scale": "x", "band": true, "offset": calculateBarGap(config)},
                    "y": {"scale": "y", "field": metadata.names[config.y]},
                    "y2": {"scale": "y", "value": 0},
                    "fill": {"value": config.markColor},
                    "fillOpacity": {"value": 1}
                  };
  }

  var mark = {
                  "type": "rect",
                  "from": {"data": config.title},
                  "properties": {
                    "update": markContent,
                    "hover": {
                      "fillOpacity": {"value": 0.5},
                      "cursor": {"value": config.hoverCursor}
                    }
                  }
              };
      

  return mark;
}

function getStackBarMark(config, metadata){
  var markContent;
  if (config.orientation == "left") {
    mark = {
        "type": "rect",
        "from": {
          "data": config.title,
          "transform": [
            { "type": "stack", 
              "groupby": [metadata.names[config.x]], 
              "sortby": [metadata.names[config.color]], 
              "field":metadata.names[config.y]}
          ]
        },
        "properties": {
          "update": {
            "y": {"scale": "x", "field": metadata.names[config.x]},
            "height": {"scale": "x", "band": true, "offset": calculateBarGap(config)},
            "x": {"scale": "y", "field": "layout_start"},
            "x2": {"scale": "y", "field": "layout_end"},
            "fill": {"scale": "color", "field": metadata.names[config.color]},
            "fillOpacity": {"value": 1}
          },
          "hover": {
            "fillOpacity": {"value": 0.5},
            "cursor": {"value": config.hoverCursor}
          }
        }
      };
  } else {

    mark = {
        "type": "rect",
        "from": {
          "data": config.title,
          "transform": [
            { "type": "stack", 
              "groupby": [metadata.names[config.x]], 
              "sortby": [metadata.names[config.color]], 
              "field":metadata.names[config.y]}
          ]
        },
        "properties": {
          "update": {
            "x": {"scale": "x", "field": metadata.names[config.x]},
            "width": {"scale": "x", "band": true, "offset": calculateBarGap(config)},
            "y": {"scale": "y", "field": "layout_start"},
            "y2": {"scale": "y", "field": "layout_end"},
            "fill": {"scale": "color", "field": metadata.names[config.color]},
            "fillOpacity": {"value": 1}
          },
          "hover": {
            "fillOpacity": {"value": 0.5},
            "cursor": {"value": config.hoverCursor}
          }
        }
      };
  }


      

  return mark;
}

function getGroupBarMark(config, metadata){
  var mark;
  if (config.orientation == "left") {
      mark =  {
          "type": "group",
          "from": {
            "data": config.title,
            "transform": [{"type":"facet", "groupby": [metadata.names[config.x]]}]
          },
          "properties": {
            "update": {
              "y": {"scale": "x", "field": "key"},
              "height": {"scale": "x", "band": true}
            }
          },
          "scales": [
            {
              "name": "pos",
              "type": "ordinal",
              "range": "height",
              "domain": {"field": metadata.names[config.color]}
            }
          ],
          "marks": [
          {
              "name": "bars",
              "type": "rect",
              "properties": {
                "update": {
                  "y": {"scale": "pos", "field": metadata.names[config.color]},
                  "height": {"scale": "pos", "band": true},
                  "x": {"scale": "y", "field": metadata.names[config.y]},
                  "x2": {"scale": "y", "value": 0},
                  "fill": {"scale": "color", "field": metadata.names[config.color]},
                  "fillOpacity": {"value": 1}
                },
                "hover": {
                  "fillOpacity": {"value": 0.5},
                  "cursor": {"value": config.hoverCursor}
                }
              }
            }
          ]
        };
  } else {
      mark =  {
          "type": "group",
          "from": {
            "data": config.title,
            "transform": [{"type":"facet", "groupby": [metadata.names[config.x]]}]
          },
          "properties": {
            "update": {
              "x": {"scale": "x", "field": "key"},
              "width": {"scale": "x", "band": true}
            }
          },
          "scales": [
            {
              "name": "pos",
              "type": "ordinal",
              "range": "width",
              "domain": {"field": metadata.names[config.color]}
            }
          ],
          "marks": [
          {
              "name": "bars",
              "type": "rect",
              "properties": {
                "update": {
                  "x": {"scale": "pos", "field": metadata.names[config.color]},
                  "width": {"scale": "pos", "band": true},
                  "y": {"scale": "y", "field": metadata.names[config.y]},
                  "y2": {"scale": "y", "value": 0},
                  "fill": {"scale": "color", "field": metadata.names[config.color]},
                  "fillOpacity": {"value": 1}
                },
                "hover": {
                  "fillOpacity": {"value": 0.5},
                  "cursor": {"value": config.hoverCursor}
                }
              }
            }
          ]
        };
  }
  return mark;
}

function calculateBarGap(config){

  var xWidth;

  if (config.orientation == "left") {
    xWidth = config.height;
  } else {
    xWidth = config.width
  }

  return  -config.barGap * (xWidth/30);

};var vizg = function(dataTable, config) {
	dataTable = buildTable(dataTable); 
	if (typeof config.charts !== "undefined" && config.charts.length == 1) {
		//Set chart config properties for main
		for (var property in config.charts[0]) {
		    if (config.charts[0].hasOwnProperty(property)) {
		        config[property] = config.charts[0][property];
		    }
		}

		this.chart =  new window[config.type]([dataTable], config);
	}
};

vizg.prototype.draw = function(div, callback) {
	this.chart.draw(div, callback);
};

vizg.prototype.insert = function(data) {
	this.chart.insert(buildData(data, this.chart.metadata));
};

vizg.prototype.getSpec = function() {
	return this.chart.getSpec();
};;var line = function(dataTable, config) {
      this.metadata = dataTable[0].metadata;
      var marks =[];
      var signals = [];
      this.spec = {};

      config = checkConfig(config, this.metadata);
      this.config = config;
      dataTable[0].name= config.title;
      
      var scales =  getXYScales(config, this.metadata);

      if (config.color != -1) {

          if (config.colorDomain == -1) {
              config.colorDomain = {"data":  config.title, "field": this.metadata.names[config.color]};
          }

          var colorScale = {
                    "name": "color", 
                    "type": "ordinal", 
                    "domain": config.colorDomain,
                    "range": config.colorScale
                      };
          scales.push(colorScale);
      } 

      var axes =  getXYAxes(config, "x", "x", "y", "y");

      marks.push(getLineMark(config, this.metadata));
      config.markSize = 20;
      marks.push(getSymbolMark(config, this.metadata));

      if (config.range) {
         signals = getRangeSignals(config, signals);
         marks = getRangeMark(config, marks);
      }

      if (config.color != -1) {

          var legendTitle = "Legend";

          if (config.title != "table") {
              legendTitle = config.title;
          }

          if (this.config.legend) {
              this.spec.legends = getLegend(this.config);
          }
       
      }
      
      this.spec.width = config.width;
      this.spec.height = config.height;
      this.spec.axes = axes;
      this.spec.data = dataTable;
      this.spec.scales = scales;
      this.spec.padding = config.padding;
      this.spec.marks = marks;
      this.spec.signals = signals;
      
};

line.prototype.draw = function(div, callbacks) {
    if(this.config.maxLength != -1){
        var dataset = this.spec.data[0].values;
        var maxValue = this.config.maxLength;
        if(dataset.length >= this.config.maxLength){
            var allowedDataSet = [];
            var startingPoint = dataset.length - maxValue;
            for(var i = startingPoint; i < dataset.length;i++){
                allowedDataSet.push(dataset[i]);
            }
            this.spec.data[0].values = allowedDataSet;
        }
    }

    drawChart(div, this, callbacks);

};

line.prototype.insert = function(data) {
    //Removing events when max value is enabled
    if (this.config.maxLength != -1 && this.config.maxLength <  (this.view.data(this.config.title).values().length + data.length)) {
        var removeFunction = (function(d) { 
          return d[this.metadata.names[this.config.x]] == oldData; 
        }).bind(this);

        for (i = 0; i < data.length; i++) {
          var oldData = this.view.data(this.config.title).values()[i][this.metadata.names[this.config.x]];
          this.view.data(this.config.title).remove(removeFunction);  
        }
    } 

     this.view.data(this.config.title).insert(data);
     this.view.update();
};

line.prototype.getSpec = function() {
  return this.spec;
};

function getLineMark(config, metadata){
        var mark;
        if (config.color != -1) {
          mark =  {
                  "name": "line-group",
                  "type": "group",
                  "from": {
                    "data":  config.title,
                    "transform": [{"type": "facet", "groupby": [metadata.names[config.color]]}]
                  },
                  "marks": [
                    {
                      "type": "line",
                      "properties": {
                        "update": {
                          "x": {"scale": "x", "field": metadata.names[config.x]},
                          "y": {"scale": "y", "field": metadata.names[config.y]},
                          "stroke": {"scale": "color", "field": metadata.names[config.color]},
                          "strokeWidth": {"value": 2},
                          "strokeOpacity": {"value": 1}
                        },
                        "hover": {
                          "strokeOpacity": {"value": 0.5},
                          "cursor": {"value": config.hoverCursor}
                        }
                      }
                    }
                  ]
                };
        } else {
            mark = {
                    "type": "line",
                    "from": {"data": config.title},
                    "properties": {
                      "update": {

                        "x": {"scale": "x", "field": metadata.names[config.x]},
                        "y": {"scale": "y", "field": metadata.names[config.y]},
                        "stroke": { "value": config.markColor},
                        "strokeWidth": {"value": 2},
                        "strokeOpacity": {"value": 1}
                      },
                      "hover": {
                        "strokeOpacity": {"value": 0.5}
                      }
                    }
                };
        }

        return mark;
}

;var map = function(dataTable, config) {

    this.metadata = dataTable[0].metadata;
    var marks ;
    var signals ;
    var predicates = [];
    var legends = [];
    this.spec = {};
    var geoInfoJson ;

    geoInfoJson = loadGeoMapCodes(config.helperUrl);
    config = checkConfig(config, this.metadata);
    this.config = config;
    this.config.geoInfoJson = geoInfoJson;

    for (i = 0; i < dataTable[0].values.length; i++) {
        for (var key in dataTable[0].values[i]) {
            if(key == dataTable[0].metadata.names[config.x]){
                if (dataTable[0].values[i].hasOwnProperty(key)) {
                    dataTable[0].values[i].unitName = dataTable[0].values[i][key];
                    dataTable[0].values[i][key] = getMapCode(dataTable[0].values[i][key], config.mapType,geoInfoJson);
                    break;
                }
            }
        }
    };

    dataTable[0].name = config.title;
    dataTable[0].transform = [
        {
            "type": "formula",
            "field": "v",
            "expr": "datum."+this.metadata.names[config.y]
        }
    ];

    if (config.tooltip.enabled) {
        marks = getMapMark(config, this.metadata);
        signals = getMapSignals();
        this.spec.signals = signals;
    }

    dataTable.push(getTopoJson(config,this.metadata));
    predicates.push(getMapPredicates());

    if (config.legend) {
        legends.push(getMapLegends(config,this.metadata));
    }

    var cScale = {
        "name": "color",
        "type": "linear",
        "domain": {"data": "geoData","field": "zipped.v"},
        "domainMin": 0.0,
        "zero": false,
        "range":  config.colorScale
    };

    var scales =  [cScale];

    this.spec.width = config.width;
    this.spec.height = config.height;
    this.spec.data = dataTable;
    this.spec.scales = scales;
    this.spec.padding = config.padding;
    this.spec.marks = marks;
    this.spec.predicates = predicates;
    this.spec.legends = legends;

};

map.prototype.draw = function(div, callbacks) {
    var viewUpdateFunction = (function(chart) {
       this.view = chart({el:div}).renderer(this.config.renderer).update();

       if (callbacks != null) {
          for (var i = 0; i<callbacks.length; i++) {
            this.view.on(callbacks[i].type, callbacks[i].callback);
          }
       }

    }).bind(this);

    vg.parse.spec(this.spec, viewUpdateFunction);
};

map.prototype.insert = function(data) {

    var xAxis = this.metadata.names[this.config.x];
    var yAxis = this.metadata.names[this.config.y];
    var color = this.metadata.names[this.config.color];
    var mapType = this.config.mapType;
    var geoInfoJson = this.config.geoInfoJson;

   for (i = 0; i < data.length; i++) {
        for (var key in data[i]) {
            if(key == xAxis){
                if (data[i].hasOwnProperty(key)) {
                    data[i].unitName = data[i][key];
                    data[i][key] = getMapCode(data[i][key], mapType,geoInfoJson);
                    break;
                }
            }
        }
    };

    for (i = 0; i < data.length; i++) {
        var isValueMatched = false;
        this.view.data(this.config.title).update(function(d) {
                return d[xAxis] == data[i][xAxis]; },
            yAxis,
            function(d) {
                isValueMatched = true;
                return data[i][yAxis];
            });

        this.view.data(this.config.title).update(function(d) {
                return d[xAxis] == data[i][xAxis]; },
            color,
            function(d) {
                isValueMatched = true;
                return data[i][color];
            });


        if(!isValueMatched){
            this.view.data(this.config.title).insert([data[i]]);
        }
    }
    this.view.update();

};

function getTopoJson(config, metadata){

    var width = config.width;
    var height = config.height;
    var scale;
    var mapType = config.charts[0].mapType;
    var projection = "mercator";

    if(mapType == "usa"){
        width = config.width - 160;
        height = config.height - 130;
        scale = config.height + 270;
        projection = "albersUsa";
    }else if(mapType == "europe"){
        width = ((config.width/2)+ 100)/2;
        height = config.height + 150;
        scale = config.height + 50;
    }else{
        scale = (config.width/640)*120;
        width = config.width/2 + 10;
        height = config.height/2+40;
    }
    var mapUrl = config.geoCodesUrl;

    var json = {

        "name": "geoData",
        "url": mapUrl,
        "format": {"type": "topojson","feature": "units"},
        "transform": [
            {
                "type": "geopath",
                "value": "data",
                "scale": scale,
                "translate": [width,height],
                "projection": projection
            },
            {
                "type": "lookup",
                "keys": ["id"],
                "on": config.title,
                "onKey": metadata.names[config.x],
                "as": ["zipped"],
                "default": {"v": null, "country":"No data"}
            }
        ]
    }

    return json;

}

function getMapMark(config, metadata){

    var mark = [

        {
            "name": "map",
            "type": "path",
            "from": {"data": "geoData"},
            "properties": {
                "enter": {"path": {"field": "layout_path"}},
                "update": {
                    "fillOpacity": {"value": 1},
                    "fill":{
                        "rule": [
                            {
                                "predicate": {
                                    "name": "isNotNull",
                                    "id": {"field": "zipped.v"}
                                },
                                "scale": "color",
                                "field": "zipped.v"
                            },
                            {"value": config.mapColor}
                        ]
                    }
                },
                "hover": {
                    "fillOpacity": {"value": 0.5},
                    "cursor": {"value": config.hoverCursor}
                }
            }
        },
        {
            "type": "group",
            "from": {"data": config.title,
                "transform": [
                    {
                        "type": "filter",
                        "test": "datum."+metadata.names[config.x]+" == tooltipSignal.datum."+metadata.names[config.x]+""
                    }
                ]},
            "properties": {
                "update": {
                    "x": {"signal": "tooltipSignal.x", "offset": -5},
                    "y": {"signal": "tooltipSignal.y", "offset": 20},
                    "width": {"value": 100},
                    "height": {"value": 20},
                    "fill": {"value": config.tooltip.color}
                }
            },
            "marks": [
                {
                    "type": "text",
                    "properties": {
                        "update": {
                            "x": {"value": 6},
                            "y": {"value": 14},
                            "text": {"template": "\u007b{tooltipSignal.datum.unitName}} \u007b{tooltipSignal.datum.v}}"},
                            "fill": {"value": "black"}
                        }
                    }
                }
            ]
        }

    ]


    return mark;
}

function getMapSignals(){

    var signals = [
        {
            "name": "tooltipSignal",
            "init": {"expr": "{x: 0, y: 0, datum: {} }"},
            "streams": [
                {
                    "type": "@map:mouseover",
                    "expr": "{x: eventX(), y: eventY(), datum: eventItem().datum.zipped}"
                },
                {
                    "type": "@map:mouseout",
                    "expr": "{x: 0, y: 0, datum: {} }"
                }
            ]
        }
    ]

    return signals;
}

function getMapPredicates(){

    var predicates = {

        "name": "isNotNull",
        "type": "!=",
        "operands": [{"value": null}, {"arg": "id"}]
    }

    return predicates;
}

function getMapLegends(config, metadata){

    var legends = {

        "fill": "color",
        "title": metadata.names[config.y],
        "properties": {
            "gradient": {
                "stroke": {"value": "transparent"}
            },
            "title": {
                "fontSize": {"value": 14}
            },
            "legend": {
                "x": {"value": 0},
                "y": {"value": config.height - 40}
            }
        }
    }

    return legends;
}

function loadGeoMapCodes(url){
    var geoMapCodes;
    var xobj = new XMLHttpRequest();
    xobj.overrideMimeType("application/json");
    xobj.open('GET', url, false);
    xobj.onreadystatechange = function () {
          if (xobj.readyState == 4 && xobj.status == "200") {
            geoMapCodes = JSON.parse(xobj.responseText);
          }
    };
    xobj.send(null); 

    return geoMapCodes;
}

function getMapCode(name, region, geoInfo) {
    if (region == "world" || region == "europe") {
        for (i = 0; i < geoInfo.length; i++) {
            if (name.toUpperCase() == geoInfo[i]["name"].toUpperCase()) {
                name = geoInfo[i]["alpha-3"];
            }
        };
    } else {
        var i = 0;
        for (var property in geoInfo) {
            if (geoInfo.hasOwnProperty(property)) {
                if(name.toUpperCase() == property.toUpperCase()){
                    name = "US"+geoInfo[property];
                }
        }
        i++;
        };
    }
    return name;
};;
var number = function(dataTable, config) {
      this.metadata = dataTable[0].metadata;
      this.data = dataTable[0].values
      var marks =[];
      this.spec = {};

      config = checkConfig(config, this.metadata);
      this.config = config;
      dataTable[0].name= config.title;

};

number.prototype.draw = function(div) {
  div = div.replace("#","");
  var contentId = div+"Content";
  var textContent = "";

  if (this.data != null && this.data.length != 0) {
      textContent = this.data[this.data.length-1][this.metadata.names[this.config.x]];    
  }

  var divContent = "<p style='padding: 0px 0px 0px 20px;'>"+this.config.title+"</p><br/>"
                  +"<p align='center' style='font-size:60px;padding: 0px 0px 0px 20px;' id='"+contentId+"'>"
                  +textContent+"</p>";

   document.getElementById(div).innerHTML = divContent;
   this.view = contentId;
};

number.prototype.insert = function(data) {
    document.getElementById(this.view).innerHTML = data[data.length-1][this.metadata.names[this.config.x]];
};



;var scatter = function(dataTable, config) {

    this.metadata = dataTable[0].metadata;
    var marks = [];
    var signals ;
    this.spec = {};

    config = checkConfig(config, this.metadata);
    this.config = config;
    dataTable[0].name = config.title;

    var rScale = {
        "name": "size",
        "type": "linear",
        "range": [0,576],
        "domain": {"data":  config.title, "field": this.metadata.names[config.size]}
    };

    var cScale = {
        "name": "color",
        "type": this.metadata.types[config.color],
        "range": config.colorScale,
        "domain": {"data":  config.title, "field": this.metadata.names[config.color]}
    };

    var scales =  getXYScales(config, this.metadata);
    scales.push(rScale);
    scales.push(cScale);

    var axes =  getXYAxes(config, "x", "x", "y", "y");

    marks.push(getScatterMark(config, this.metadata));

    if (this.config.legend 
        && this.metadata.types[config.color] != "linear") {
         this.spec.legends = getLegend(this.config);
    }

    this.spec.width = config.width;
    this.spec.height = config.height;
    this.spec.axes = axes;
    this.spec.data = dataTable;
    this.spec.scales = scales;
    this.spec.padding = config.padding;
    this.spec.marks = marks;

};

scatter.prototype.draw = function(div, callbacks) {
    var viewUpdateFunction = (function(chart) {
      if(this.config.tooltip.enabled){
         createTooltip(div);
         this.view = chart({el:div}).renderer(this.config.renderer).update();
         bindTooltip(div,this.view,this.config,this.metadata);
      } else {
         this.view = chart({el:div}).renderer(this.config.renderer).update();
      }
       if (callbacks != null) {
          for (var i = 0; i<callbacks.length; i++) {
            this.view.on(callbacks[i].type, callbacks[i].callback);
          }
       }

    }).bind(this);
    
    if(this.config.maxLength != -1){
        var dataset = this.spec.data[0].values;
        var maxValue = this.config.maxLength;
        if(dataset.length >= this.config.maxLength){
            var allowedDataSet = [];
            var startingPoint = dataset.length - maxValue;
            for(var i = startingPoint; i < dataset.length;i++){
                allowedDataSet.push(dataset[i]);
            }
            this.spec.data[0].values = allowedDataSet;
        }
    }

    vg.parse.spec(this.spec, viewUpdateFunction);
};

scatter.prototype.insert = function(data) {

    var xAxis = this.metadata.names[this.config.x];
    var yAxis = this.metadata.names[this.config.y];
    var size = this.metadata.names[this.config.size];
    var color = this.metadata.names[this.config.color];

    if (this.config.maxLength != -1 && this.config.maxLength <  (this.view.data(this.config.title).values().length + data.length)) {

        var allDataSet = this.view.data(this.config.title).values().concat(data);
        var allowedRemovableDataSet = [];
        for (i = 0; i < allDataSet.length - this.config.maxLength; i++) {
            allowedRemovableDataSet.push(this.view.data(this.config.title).values()[i][xAxis]);
        }

        for (i = 0; i < data.length; i++) {
            var isValueMatched = false;
            this.view.data(this.config.title).update(function(d) {
                    return d[xAxis] == data[i][xAxis]; },
                yAxis,
                function(d) {
                    isValueMatched = true;
                    return data[i][yAxis];
                });

            this.view.data(this.config.title).update(function(d) {
                    return d[xAxis] == data[i][xAxis]; },
                color,
                function(d) {
                    isValueMatched = true;
                    return data[i][color];
                });

            this.view.data(this.config.title).update(function(d) {
                    return d[xAxis] == data[i][xAxis]; },
                size,
                function(d) {
                    isValueMatched = true;
                    return data[i][size];
                });

            if(isValueMatched){
                var isIndexRemoved = false;

                var index = allowedRemovableDataSet.indexOf(data[i][xAxis]);
                if (index > -1) {
                    // updated value matched in allowed removable values
                    isIndexRemoved = true;
                    allowedRemovableDataSet.splice(index, 1);
                }

                if(!isIndexRemoved){
                    // updated value NOT matched in allowed removable values
                    allowedRemovableDataSet.splice((allowedRemovableDataSet.length - 1), 1);
                }

            } else {
                //insert the new data
                this.view.data(this.config.title).insert([data[i]]);
                this.view.update();
            }
        }

        var oldData;
        var removeFunction = function(d) {
            return d[xAxis] == oldData;
        };

        for (i = 0; i < allowedRemovableDataSet.length; i++) {
            oldData = allowedRemovableDataSet[i];
            this.view.data(this.config.title).remove(removeFunction);
        }
    } else{
        for (i = 0; i < data.length; i++) {
            var isValueMatched = false;
            this.view.data(this.config.title).update(function(d) {
                    return d[xAxis] == data[i][xAxis]; },
                yAxis,
                function(d) {
                    isValueMatched = true;
                    return data[i][yAxis];
                });

            this.view.data(this.config.title).update(function(d) {
                    return d[xAxis] == data[i][xAxis]; },
                color,
                function(d) {
                    isValueMatched = true;
                    return data[i][color];
                });

            this.view.data(this.config.title).update(function(d) {
                    return d[xAxis] == data[i][xAxis]; },
                size,
                function(d) {
                    isValueMatched = true;
                    return data[i][size];
                });

            if(!isValueMatched){
                this.view.data(this.config.title).insert([data[i]]);
            }
        }
    }
    this.view.update({duration: 200});
};

scatter.prototype.getSpec = function() {
    return this.spec;
};


function getScatterMark(config, metadata){
    var fill;
    var size;

    if (config.color == -1) {
        fill = {"value": config.markColor};
    } else {
        fill = {"scale": "color", "field": metadata.names[config.color]};
    }

    if (config.size == -1) {
        size = {"value": config.markSize * 50};
    } else {
        size = {"scale":"size","field":metadata.names[config.size]};
    }

    var mark = {

            "type": "symbol",
            "from": {"data": config.title},
            "properties": {
                "update": {
                    "x": {"scale": "x", "field": metadata.names[config.x]},
                    "y": {"scale": "y", "field": metadata.names[config.y]},
                    "fill": fill,
                    "size": size,
                    "fillOpacity": {"value": 1}
                },
                "hover": {
                    "fillOpacity": {"value": 0.5},
                    "cursor": {"value": config.hoverCursor}
                }
            }

        }
    ;


    return mark;
}

function getScatterToolTipMark(config, metadata) {
    config.toolTip.height = 50;
    config.toolTip.y = -50;

    var mark = getToolTipMark(config, metadata);
    var sizeText = {
        "type": "text",
        "properties": {
            "update": {
                "x": {"value": 6},
                "y": {"value": 44},
                "text": {"template": "Size \t (" + metadata.names[config.size] + ") \t {{hover." + metadata.names[config.size] + "}}"},
                "fill": {"value": "black"}
            }
        }
    };
    mark.marks.push(sizeText);
    return mark;
}
;
var table = function(dataTable, config) {
      this.metadata = dataTable[0].metadata;
      this.data = dataTable[0].values
      var marks =[];
      this.spec = {};
      config = checkConfig(config, this.metadata);
      this.config = config;
      dataTable[0].name= config.title;

};

table.prototype.draw = function(div) {
  var table = d3.select(div).append("table")
                .attr( "class", "table table-bordered")
                .attr("id", this.config.title);

      // set up the table header
      table.append('thead').attr("align", "center")
          .append('tr') 
          .selectAll('th')
              .data(this.config.columnTitles)
          .enter()
              .append('th')
              .html(function (d) { return d });

      table.append('tbody').attr("id", "tableChart-"+this.config.title);
      this.setupData(this.data, this.config);

      table.selectAll("thead th")
      .html(function(column) {
          return column.charAt(0).toUpperCase() + column.substr(1);
      });
  
      
      };

table.prototype.insert = function(data) {
    this.setupData(data, this.config);
};


table.prototype.setupData = function (dataset, config) {
    var data = [];
    var allColumns = this.metadata.names;
    
    //Select specified columns from dataset
    for (var i = 0; i < dataset.length; i++) {
        var row = {};

        for (var x = 0; x < config.columns.length; x++) {
            row[config.columns[x]] = dataset[i][config.columns[x]];
        }
        data.push(row);
    }

   //Select Rows by x Axis
    var rows = d3.select('#tableChart-'+config.title)
        .selectAll('tr')
        .data(data, function(d) { return d[config.key]})

    var entertd = rows.enter()
        .append('tr')
            .selectAll('td')
               .data(function(row) {
                return config.columns.map(function(column) {
                    return {column: column, value: row[column]};
                });
            })
            .enter()
            .append('td')

    //Color cell background
    if (config.color != -1) {
            d3.select('#tableChart-'+config.title)
                  .selectAll('td')
                      .attr('bgcolor',
                        function(d) { 
                            var column = d.key  || d.column;

                                var color;
                                if (typeof config.colorScale == "string") {
                                  color = window["d3"]["scale"][config.colorScale]().range();
                                } else {
                                  color = config.colorScale;
                                }

                                var colorIndex;
                                for(var i = 0; i < allColumns.length; i += 1) {
                                if(allColumns[i] === column) {
                                    colorIndex = i;
                                }
                            }

                            if (typeof d.value == "string") {

                                      var colorDomain;

                               if (config.colorDomain == -1) {
                                colorDomain = [d3.min(d3.select('#tableChart-'+config.title) .selectAll('tr') .data(), function(d) { return d[column]; }), 
                                              d3.max(d3.select('#tableChart-'+config.title) .selectAll('tr') .data(), function(d) { return d[column]; })]

                               } else {
                                  colorDomain = config.colorDomain
                               }

                                var colorScale = d3.scale.ordinal()
                                                .range(config.colorScale)
                                                .domain(colorDomain);
                                return colorScale(d.value); 

                            } else if (config.color == "*" || column == allColumns[config.color]){

                                var colorScale = d3.scale.linear()
                                                .range(['#f2f2f2', color[colorIndex]])
                                                .domain([d3.min(d3.select('#tableChart-'+config.title) .selectAll('tr') .data(), function(d) { return d[column]; }), 
                                                         d3.max(d3.select('#tableChart-'+config.title) .selectAll('tr') .data(), function(d) { return d[column]; })]
                                                        );
                                
                                return colorScale(d.value); 
                            }

            });
    }

                
              
    entertd.append('span')
    var td = rows.selectAll('td')
    .style({"padding": "0px 10px 0px 10px"})

        .data(function(d) { return d3.map(d).entries() })
        .attr('class', function (d) { return d.key })


    

    td.select('span')
        .html(function(d) {
            return d.value
        })
    //Remove data items when it hits maxLength 
    if (config.maxLength != -1 && d3.select('tbody').selectAll('tr').data().length > config.maxLength) {
          var allowedDataset = d3.select('tbody').selectAll('tr').data().slice(d3.select('tbody').selectAll('tr').data().length- config.maxLength, config.maxLength);
          d3.select('tbody').selectAll('tr').data(allowedDataset, 
            function(d) { 
              return(d); 
            })  
          .remove();
    }
};var extend = function (defaults, options) {
    var extended = {};
    var prop;
    for (prop in defaults) {
        if (Object.prototype.hasOwnProperty.call(defaults, prop)) {
            extended[prop] = defaults[prop];
        }
    }
    for (prop in options) {
        if (Object.prototype.hasOwnProperty.call(options, prop)) {
            extended[prop] = options[prop];
        }
    }
    return extended;
};

function checkConfig(config, metadata){
    
    if (config.color == null) {
		config.color = -1;
	} else if (config.color != "*"){
		config.color = metadata.names.indexOf(config.color);
	}

    if (config.size == null) {
        config.size = -1;
    } else {
        config.size = metadata.names.indexOf(config.size);
    }

    var defaults = {
        title: "table",
        mapType: -1,
        mode: "stack",
        //color hex array or string: category10, 10c, category20, category20b, category20c
        colorScale: "category10", 
        colorDomain: -1,
        maxLength: -1,
        markSize: 2,
        fillOpacity: 1,
        innerRadius:0,
        //string: canvas or svg
        renderer: "svg", 
        padding: {"top": 10, "left": 50, "bottom": 40, "right": 50},
        dateFormat: "%x %X",
        range:false,
        rangeColor:"#222",
        selectionColor:"#222",
        barGap:1,
        mapColor:"#888",
        hoverCursor:"pointer",
        rangeCursor:"grab",

        textColor:"#888",

        //Tool Configs
        tooltip: {"enabled":true, "color":"#e5f2ff", "type":"symbol"},

        //Legend Configs
        legend:true,
        legendTitle: "Legend",
        legendTitleColor: "#222",
        legendTitleFontSize: 13,
        legendTextColor: "#888",
        ledgendTextFontSize: 12,

        //Axes Configs
        xTitle: config.x,
        yTitle: config.y,
        xAxisAngle:false,
        yAxisAngle:false,

        xAxisStrokeSize:0,
        xAxisColor:"#222",
        xAxisSize:1,
        xAxisFontSize:10,
        xAxisTitleFontSize:12,
        xAxisTitleFontColor:"#222",

        yAxisStrokeSize:0,
        yAxisColor:"#222",
        yAxisSize:1,
        yAxisFontSize:10,
        yAxisTitleFontSize:12,
        yAxisTitleFontColor:"#222",

        grid: true,
        zero: false,
        xTicks: 0,
        yTicks: 0,
        xFormat: "",
        yFormat: "",

        xScaleDomain: null,
        yScaleDomain: null


    };
    
    if (typeof vizgSettings != 'undefined'){
        defaults = extend(defaults, vizgSettings);
    }

    config = extend(defaults, config);
    config.height = config.height  - (config.padding.top + config.padding.bottom);
    config.width = config.width  - (config.padding.left + config.padding.right);

    if (typeof config.colorScale == "string") {
      config.markColor = window["d3"]["scale"][config.colorScale]().range()[0];
    } else {
      config.markColor = config.colorScale[0];
    }

	  config.x = metadata.names.indexOf(config.x);
    config.y = metadata.names.indexOf(config.y);
    
    if (config.xScaleDomain == null) {
      config.xScaleDomain = {"data":  config.title, "field": metadata.names[config.x]};
    }

    if (config.yScaleDomain == null) {
      config.yScaleDomain = {"data":  config.title, "field": metadata.names[config.y]};
    }
    
    return config;
}

function buildTable(datatable) {
	var chartDatatable = {};
	chartDatatable.metadata = datatable[0].metadata;
	chartDatatable.values = buildData(datatable[0].data, datatable[0].metadata);
	return chartDatatable;
}


function buildData(data, metadata) {
	chartData = [];
	for (i = 0; i < data.length; i++) {
		var row = {};
		for (x = 0; x < metadata.names.length; x++) {
			row[metadata.names[x]] = data[i][x];
		}
		chartData.push(row);
	}
	return chartData;
}

/*
	General function used to draw circle symbols graphs
*/
function getSymbolMark(config, metadata) {

  var fill;
  if (config.color != -1) { 
      fill =  {"scale": "color", "field": metadata.names[config.color]};
  } else {
      fill = {"value":config.markColor};
  }

var mark = {
      "name": "points-group",
      "type": "symbol",
      "from": {"data": config.title},
      "properties": {
        "update": {
          "x": {"scale": "x", "field": metadata.names[config.x]},
          "y": {"scale": "y", "field": metadata.names[config.y]},
          "fill": fill,
          "size": {"value": config.markSize},
          "fillOpacity": {"value": config.fillOpacity}
        }, 
        "hover" : {
          "cursor": {"value": config.hoverCursor}
        }
      }
    }

    return mark;
}

function getSignals(config, metadata){

    var signals = [{
            "name": "hover",
            "init": {},
            "streams": [
                {"type": config.hoverType+":mouseover", "expr": "datum"},
                {"type": config.hoverType+":mouseout", "expr": "{}"}
            ]
    }];

    return signals;

}

function bindTooltip(div,markType,eventObj, config, metaData, keyList){

    eventObj.on("mouseover", function(event, item) {

        if (item != null && item.status != "exit" && item.mark.marktype == markType) {
            var canvas = $(".marks")[0];
            if($("#wrapper #tip").length) {
                $tip.remove();
            }

            $(div).wrap( "<div id='wrapper' style='position: relative'></div>" );

            $("#wrapper").append("<div id='tip' class='tooltipClass' style='top:0; left: 0; position: absolute'></div>");
            $tip=$('#tip');
            $tip.empty();

            var dataObj = item.datum;
            var dynamicContent = "";
            for (var key in dataObj) {
                if (dataObj.hasOwnProperty(key)) {
                    if(keyList != undefined){
                        for(var z=0;z<keyList.length;z++){
                            for (var keyVal in config) {
                                if(keyVal == keyList[z] && metaData.names[config[keyVal]] == key){
                                    dynamicContent += "<p>"+keyList[z]+" ("+key+"):"+dataObj[key]+"</p>";
                                    break;
                                }
                            }
                        }
                    }else{
                        if(metaData.names[config.x] == key){
                            dynamicContent += "<p>X ("+key+"):"+dataObj[key]+"</p>";
                        }
                        if(metaData.names[config.y] == key){
                            dynamicContent += "<p>Y ("+key+"):"+dataObj[key]+"</p>";
                        }
                    }
                }
            }

            $tip.append(dynamicContent);

            var canvasWidth = canvas.width;
            var canvasHeight = canvas.height;

            var el = $('.marks[style*="width"]');

            if(el.length > 0){
                canvasWidth = parseFloat($(".marks")[0].style.width);
                canvasHeight = parseFloat($(".marks")[0].style.height);
            }
            var dynamicWidth = $tip.width();
            var dynamicHeight = $tip.height();

            var toolTipWidth = item.bounds.x2 + config.padding.left + dynamicWidth;
            var toolTipHeight = (canvasHeight - item.bounds.y2) - config.padding.top + dynamicHeight;
            var toolTipCalculatedXPosition;
            var toolTipCalculatedYPosition = ((item.bounds.y2 + config.padding.top) - dynamicHeight);

            if(toolTipWidth > canvasWidth){
                toolTipCalculatedXPosition = ((item.bounds.x2 + config.padding.left) - dynamicWidth);
            }else{
                toolTipCalculatedXPosition = (item.bounds.x2 + config.padding.left);
            }

            if(toolTipHeight > canvasHeight){
                toolTipCalculatedYPosition = item.bounds.y2 + config.padding.top;
            }

            $tip.css({left:toolTipCalculatedXPosition,top:toolTipCalculatedYPosition}).show();
        }else{

            if($("#wrapper #tip").length) {
                $tip.remove();
            }
            if($(div).closest("#wrapper").length) {
                $(div).unwrap();
            }
        }
    })
};

function createTooltip(div) {
   document.getElementById(div.replace("#", "")).innerHTML = document.getElementById(div.replace("#", "")).innerHTML 
        + "<div id= "+div.replace("#", "")+"-tooltip></div>";
}

function bindTooltip(div, view, config, metadata){

    view.on("mouseover", function(event, item) {
      if (item != null && item.mark.marktype == config.tooltip.type) {
        var row =  item.datum;
        if (item.datum != null && item.datum.a != null) {
           row = item.datum.a; 
        }

        var tooltipDiv = document.getElementById(div.replace("#", "")+"-tooltip");
        var tooltipContent = "";
    
        if (row[metadata.names[config.x]]!= null) {
          var content;

        //Default tooltip content if tooltip content is not defined
        if (config.tooltip.content == null) {
              if (metadata.types[config.x]== "time") {
                var dFormat =  d3.time.format(config.dateFormat);
                content =  dFormat(new Date(parseInt(row[metadata.names[config.x]])));
              } else {
                content = row[metadata.names[config.x]];
              }

              tooltipContent += "<b>"+ metadata.names[config.x] +"</b> : "+content+"<br/>" ;

            if (row[metadata.names[config.y]] != null) {
                    tooltipContent += "<b>"+ metadata.names[config.y] + "</b> : "+row[metadata.names[config.y]]+"<br/>" 
                }
            
            } else {
                //check all specified column and add them as tooltip content
                for (var i = 0; i < config.tooltip.content.length; i++) {
                    if (metadata.types[metadata.names.indexOf(config.tooltip.content[i])]=== "time") {
                        var dFormat =  d3.time.format(config.dateFormat);
                        content =  dFormat(new Date(parseInt(row[metadata.names[config.x]])));
                    } else {
                        content = row[config.tooltip.content[i]];
                    }

                    if (config.tooltip.label != false) {
                        tooltipContent += "<b>"+ config.tooltip.content[i] +"</b> : "+content+"<br/>" ;
                    } else {
                        tooltipContent += content+"<br/>" ;
                    }
                };

        }

       
        } 


        if (tooltipContent != "") {
            tooltipDiv.innerHTML = tooltipContent;
            tooltipDiv.style.padding = "5px 5px 5px 5px";
        }

        window.onmousemove = function (e) {
          tooltipDiv.style.top = (e.clientY + 15) + 'px';
          tooltipDiv.style.left = (e.clientX + 10) + 'px';
          tooltipDiv.style.zIndex  = 1000;
          tooltipDiv.style.backgroundColor = config.tooltip.color;
          tooltipDiv.style.position = "fixed";

          if (tooltipDiv.offsetWidth +  e.clientX - (cumulativeOffset(document.getElementById(div.replace("#", ""))).left + config.padding.left)  >  document.getElementById(div.replace("#", "")).offsetWidth) {
            tooltipDiv.style.left = (e.clientX - tooltipDiv.offsetWidth) + 'px';
          }

          if (e.clientY - (cumulativeOffset(document.getElementById(div.replace("#", ""))).top + 500) >  document.getElementById(div.replace("#", "")).offsetHeight) {
            tooltipDiv.style.top = (e.clientY - 400) + 'px';
          }
        
        }; 
      }
    })
    .on("mouseout", function(event, item) {
      var tooltipDiv = document.getElementById(div.replace("#", "")+"-tooltip");
      tooltipDiv.style.padding = "0px 0px 0px 0px";
      tooltipDiv.innerHTML = "";
    }).update();
}


function cumulativeOffset(element) {
    var top = 0, left = 0;
    do {
        top += element.offsetTop  || 0;
        left += element.offsetLeft || 0;
        element = element.offsetParent;
    } while(element);

    return {
        top: top,
        left: left
    };
};

function getXYAxes(config, xAxesType, xScale, yAxesType, yScale) {
    var xProp = {"ticks": {
                   "stroke": {"value": config.xAxisColor}, 
                   "strokeWidth":{"value":config.xAxisStrokeSize}
                 },
                 "labels": {
                   "fill": {"value": config.xAxisColor},
                    "fontSize": {"value": config.xAxisFontSize}
                 },
                 "title": {
                   "fontSize": {"value": config.xAxisTitleFontSize},
                    "fill": {"value": config.xAxisTitleFontColor}
                 },
                 "axis": {
                   "stroke": {"value": config.xAxisColor},
                   "strokeWidth": {"value": config.xAxisSize}
                 }};
    var yProp =  {"ticks": {
                   "stroke": {"value": config.yAxisColor}, 
                   "strokeWidth":{"value":config.yAxisStrokeSize}
                 },
                 "labels": {
                   "fill": {"value": config.yAxisColor},
                    "fontSize": {"value": config.yAxisFontSize}
                 },
                 "title": {
                   "fontSize": {"value": config.yAxisTitleFontSize},
                    "fill": {"value": config.yAxisTitleFontColor}
                 },
                 "axis": {
                   "stroke": {"value": config.yAxisColor},
                   "strokeWidth": {"value": config.yAxisSize}
                 }};
    
    if (config.xAxisAngle) {
        xProp.labels.angle = {"value": 45};
        xProp.labels.align = {"value": "left"};
        xProp.labels.baseline = {"value": "middle"};
    }

    if (config.yAxisAngle) {
        yProp.labels.angle = {"value": 45};
        yProp.labels.align = {"value": "left"};
        yProp.labels.baseline = {"value": "middle"};
    }

    var axes =  [
      { 
        "type": xAxesType, 
        "scale": xScale,
        "grid": config.grid, 
        "format" : config.xFormat, 
        "ticks" : config.xTicks, 
        "title": config.xTitle,
        "properties": xProp
      },
      {
        "type": yAxesType, 
        "scale": yScale, 
        "grid": config.grid, 
        "format" : config.yFormat, 
        "ticks" : config.yTicks, 
        "title": config.yTitle,
        "properties": yProp
      }
    ];

    return axes;
}

function getXYScales(config, metadata) {
    var xScale = {
        "name": "x",
        "type": metadata.types[config.x],
        "range": "width",
        "zero": config.zero,
        "domain": config.xScaleDomain
    };

  var yScale = {
        "name": "y",
        "type": metadata.types[config.y],
        "range": "height",
        "zero": config.zero,
        "domain": config.yScaleDomain
    };

  return [xScale, yScale];
}

function getRangeSignals(config, signals) {
    signals.push({
            "name": "range_start",
            "streams": [{
              "type": "mousedown", 
              "expr": "eventX()", 
              "scale": {"name": "x", "invert": true}
            }]
          });
          signals.push(    {
            "name": "range_end",
            "streams": [{
              "type": "mousedown, [mousedown, window:mouseup] > window:mousemove",
              "expr": "clamp(eventX(), 0, "+config.width+")",
              "scale": {"name": "x", "invert": true}
            }]
    });
    return signals;
}

function getRangeMark(config, marks) {
      marks.push({
          "type": "rect",
          "properties":{
            "enter":{
              "y": {"value": 0},
              "height": {"value":config.height},
              "fill": {"value": config.rangeColor},
              "fillOpacity": {"value":0.3}
            },
            "update":{
              "x": {"scale": "x", "signal": "range_start"},
              "x2": {"scale": "x", "signal": "range_end"}
            }
          }
        });

     return marks;
}

function getLegend(config) {
  var legends = [
          {
            "name": "legend",
            "fill": "color",
            "title": config.legendTitle,
            "offset": 0,
            "properties": {
                  "symbols": {
                      "stroke": {"value": "transparent"}
                  },
                  "title": {
                      "fill": {"value": config.legendTitleColor},
                      "fontSize": {"value": config.legendTitleFontSize}
                  },
                  "labels": {
                      "fill": {"value": config.legendTextColor},
                      "fontSize": {"value": config.ledgendTextFontSize}
                    }
              }
          }
      ];

    return legends;
}

function drawChart(div, obj, callbacks) {
    var viewUpdateFunction = (function(chart) {
      if(obj.config.tooltip.enabled){
         createTooltip(div);
         obj.view = chart({el:div}).renderer(obj.config.renderer).update();
         bindTooltip(div,obj.view,obj.config,obj.metadata);
      } else {
         obj.view = chart({el:div}).renderer(obj.config.renderer).update();
      }

      if (callbacks != null) {
        for (var i = 0; i<callbacks.length; i++) {
          if (callbacks[i].type == "range") {
              var range_start;
              var range_end;
              var callback = callbacks[i].callback;
                if (obj.config.range) {
                  document.getElementById(div.replace("#", "")).style.cursor="grab";
                  obj.view.onSignal("range_start", function(signalName, signalValue){
                  range_start = signalValue;
                  });

                  obj.view.onSignal("range_end", function(signalName, signalValue){
                  range_end = signalValue;
                  callback(range_start, range_end);
               });
              }
          } else {
            obj.view.on(callbacks[i].type, callbacks[i].callback);
          }          
        }
      }
    }).bind(obj);

    vg.parse.spec(obj.spec, viewUpdateFunction);
}
;
var stack = function(dataTable, config) {
      this.barChart = new bar(dataTable, config);
      this.metadata = this.barChart.metadata;

      var spec = this.barChart.getSpec();

      spec.axes = [spec.axes[0]];
      spec.axes[0].grid = false;

      spec.data.push(   
                {
                  "name": "selectedPoints",
                  "modify": [
                    {"type": "clear", "test": "!multi"},
                    {"type": "toggle", "signal": "clickedPoint", "field": "id"}
                  ]
                });

      spec.signals.push(    {
                    "name": "clickedPoint",
                    "init": 0,
                    "verbose": true,
                    "streams": [{"type": "click", "expr": "datum._id"}]
                  },
                  {
                    "name": "multi",
                    "init": false,
                    "verbose": true,
                    "streams": [{"type": "click", "expr": "datum._id"}]
                  });

      var textMark =  JSON.parse(JSON.stringify(spec.marks[0]));
      textMark.type = "text";
      textMark.properties.update.text = {"field" :spec.marks[0].properties.update.fill.field};
      textMark.properties.update.x.offset = 10;
      textMark.properties.update.y.offset = -5;
      textMark.properties.update.fill = {"value": config.legendTitleColor};
      delete textMark.properties.update.y2;
      delete textMark.properties.hover;
      spec.marks.push(textMark);

      delete spec.marks[0].properties.hover;
      spec.marks[0].properties.update.fill = [
            {
              "test": "indata('selectedPoints', datum._id, 'id')",
              "value": config.selectionColor
            },spec.marks[0].properties.update.fill
          ];

      this.barChart.setSpec(spec);

};

stack.prototype.draw = function(div, callbacks) {
       this.barChart.draw(div, callbacks); 
};

stack.prototype.insert = function(data) {
     this.barChart.insert(data); 
};

stack.prototype.getSpec = function() {
  return  this.barChart.getSpec();
};

