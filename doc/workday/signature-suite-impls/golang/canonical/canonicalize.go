package canonical

import (
	"encoding/json"
	"errors"
	"reflect"
)

// Marshal wraps json.Marshal with an extra pass through a
// map[string]interface{} in order to alphabetize the fields in the generated
// JSON output.
// This will err if passed a non-struct value.
func Marshal(v interface{}) ([]byte, error) {
	inputType := reflect.TypeOf(v)
	if inputType.Elem().Kind() != reflect.Struct {
		return nil, errors.New("out type must point to a struct")
	}
	bytes, e := json.Marshal(v)
	if e != nil {
		return nil, e
	}
	var data map[string]interface{}
	if err := json.Unmarshal(bytes, &data); err != nil {
		return nil, err
	}
	marshal, err := json.Marshal(data)
	if err != nil {
		return nil, err
	}
	return marshal, err
}
