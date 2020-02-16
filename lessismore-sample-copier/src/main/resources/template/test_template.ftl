package ${packageName};

<#list imports as item>
import ${item};
</#list>

/** This is Less-is-More generating codes. Not allowed to edit. **/
public class ${name} extends Copier<${source.name}, ${target.name}> {
    public  ${name}() {
        super(${source.name}.class, ${target.name}.class);
    }

    @Override
    public ${target.name} copy(${source.name} source, Class<${target.name}> type, ${target.name} defaultValue) {
        if (source == null) {
            return defaultValue;
        }

${target.name} target = new ${target.name}();

<#list assignInfos as item>
    ${item.assignmentStatement};
</#list>

        return target;
    }
}
