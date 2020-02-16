package ${packageName};

<#list imports as item>
import ${item};
</#list>

/** This is Less-is-More generating codes. Not allowed to edit. **/
public class ${name} implements ConverterConfiguration {

    private List<CopierInterface> copies = new ArrayList<CopierInterface>();

    public ${name}() {
<#list copiers as item>
        copies.add(new Copier<${item.source.name}, ${item.target.name}>() {
            @Override
            public ${item.target.name} copy(${item.source.name} source, Class<${item.target.name}> type, ${item.target.name} defaultValue) {
                return copyFrom${item.source.name}To${item.target.name}(source, type, defaultValue);
            }
        });
</#list>
    }

    @Override
    public List<CopierInterface> loadCopiers() {
        return copies;
    }


<#list copiers as item>
    public static ${item.target.name} copyFrom${item.source.name}To${item.target.name}(${item.source.name} source, Class<${item.target.name}> type, ${item.target.name} defaultValue) {
        if (source == null) {
            return defaultValue;
        }

        ${item.target.name} target = new ${item.target.name}();

        <#list item.assignInfos as assign>
        ${assign.assignmentStatement};
        </#list>

        return target;
    }
</#list>
}
