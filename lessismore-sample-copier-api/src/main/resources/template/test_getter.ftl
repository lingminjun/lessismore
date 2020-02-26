package ${model.packageName};

import ${model.className};
<#list others?keys as key>
import ${others[key].className};
</#list>

/** This is Less-is-More generating codes. Not allowed to edit. **/
public class ${model.name}Getter extends ${model.name} {
<#list model.reverseFields as item>
    public ${item.type} ${item.getterName}Test() {
        return this.${item.name};
    }
</#list>
}
