export interface Item {
    id: string;
    name: string;
    parent: string;
    sort: number;
}

export interface TreeNode extends Item {
    children?: TreeNode[]; // 将 children 属性设置为可选
}

export function buildTree(items: Item[], parentId: string): TreeNode[] {
    return items
        .filter(item => item.parent === parentId)
        .map(item => {
            const children = buildTree(items, item.id); // 递归查找子节点

            // 如果有子节点，添加 children 属性
            if (children.length > 0) {
                return {
                    ...item,
                    children: children
                };
            } else {
                // 没有子节点时，不添加 children 属性
                return {
                    ...item
                };
            }
        });
}
export class Tool {
    /**
     * 空校验 null或""都返回true
     */
    public static isEmpty (obj: any) {
        if ((typeof obj === 'string')) {
            return !obj || obj.replace(/\s+/g, "") === ""
        } else {
            return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
        }
    }

    /**
     * 非空校验
     */
    public static isNotEmpty (obj: any) {
        return !this.isEmpty(obj);
    }

    /**
     * 对象复制
     * @param obj
     */
}
