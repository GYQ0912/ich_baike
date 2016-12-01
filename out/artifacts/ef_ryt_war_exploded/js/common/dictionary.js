/**
 * Created by Administrator on 2016/8/22.
 */
var dictionary_array = [
{id:301, label:"国家级非遗传承人"},
{id:302, label:"省级非遗传承人"},
{id:303, label:"市级非遗传承人"},
{id:304, label:"县级非遗传承人"},
{id:305, label:"手工艺人"}
];

function getLabelbyId(id) {
    for(var i = 0; i < dictionary_array.length; i++) {
        var item = dictionary_array[i];
        if(item.id == id ) {
            return item.label;
        }
    }
    return null;
}
