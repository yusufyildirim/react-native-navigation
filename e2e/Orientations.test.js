
const Utils = require('./Utils');

const elementByLabel = Utils.elementByLabel;

describe('orientation', () => {
  beforeEach(async () => {
    await device.relaunchApp();
    await device.setOrientation('portrait');
  });

  it('orientation change from contained view controller', async () => {
    await elementByLabel('Push Options Screen').tap();
    await device.setOrientation('landscape');
    await expect(element(by.id('currentOrientation'))).toHaveText('Landscape');
  });

  it('orientation should not change in modal', async () => {
    await elementByLabel('Show Modal').tap();
    await expect(element(by.id('currentOrientation'))).toHaveText('Portrait');
    await device.setOrientation('landscape');
    await expect(element(by.id('currentOrientation'))).toHaveText('Portrait');
  });
});
