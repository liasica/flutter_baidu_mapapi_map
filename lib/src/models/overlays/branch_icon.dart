/// 新增+ BranchIcon
class BranchIcon {
  /// 电池型号
  late String batteryModel;

  /// 电池数量
  late int number;

  /// 图标缩放比例
  late double scale;

  BranchIcon({
    required this.batteryModel,
    required this.number,
    required this.scale,
  });

  /// map => BMFCoordinate
  BranchIcon.fromMap(Map map)
      : assert(map.containsKey('batteryModel'), '必须有电池型号'),
        assert(map.containsKey('number'), '必须有电池数量'),
        assert(map.containsKey('scale'), '必须有缩放比例') {
    batteryModel = map['batteryModel'] as String;
    number = map['number'] as int;
    scale = map['scale'] as double;
  }

  Map<String, Object?> toMap() {
    return {
      'batteryModel': batteryModel,
      'number': number,
      'scale': scale,
    };
  }
}
