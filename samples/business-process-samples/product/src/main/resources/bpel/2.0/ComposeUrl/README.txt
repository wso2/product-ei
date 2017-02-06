Display the use of compose-url(template, pairs) xpath function.
This function expands the template URL by substituting place holders in the template, for example, ('/order/{id}', 'id', 5) returns '/order/5'.
Substitute values are either name/value pairs passed as separate parameters, or a node-set returning elements with name mapping to value.
The functions applies proper encoding to the mapped values. Undefined variables are replaced with an empty string. This function returns an URL.