package canonical

import (
	"reflect"
	"testing"
)

func TestMarshal(t *testing.T) {
	type args struct {
		v interface{}
	}
	tests := []struct {
		name    string
		args    args
		want    string
		wantErr bool
	}{
		{
			name: "Flat structure",
			args: args{
				v: &struct {
					B string
					A string
				}{
					B: "Value2",
					A: "Value1",
				},
			},
			want:    `{"A":"Value1","B":"Value2"}`,
			wantErr: false,
		},
		{
			name: "Structure with ordered array",
			args: args{
				v: &struct {
					B []string
					A string
				}{
					A: "Value1",
					B: []string{"a", "z", "b"},
				},
			},
			want:    `{"A":"Value1","B":["a","z","b"]}`,
			wantErr: false,
		},
		{
			name: "Hierarchical structure",
			args: args{
				v: &struct {
					A string
					B []struct {
						D string
						C string
					}
				}{
					A: "Value1",
					B: []struct {
						D string
						C string
					}{{"2", "1"}, {"6", "5"}, {"4", "3"}},
				},
			},
			want:    `{"A":"Value1","B":[{"C":"1","D":"2"},{"C":"5","D":"6"},{"C":"3","D":"4"}]}`,
			wantErr: false,
		},
		{
			name: "Not a structure",
			args: args{
				v: &[]string{"Array"},
			},
			want:    ``,
			wantErr: true,
		},
	}
	for _, tt := range tests {
		tt := tt
		t.Run(tt.name, func(t *testing.T) {
			got, err := Marshal(tt.args.v)
			if (err != nil) != tt.wantErr {
				t.Errorf("Marshal() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if !reflect.DeepEqual(string(got), tt.want) {
				t.Errorf("Marshal() got = %v, want %v", string(got), tt.want)
			}
		})
	}
}
